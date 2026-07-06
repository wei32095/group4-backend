package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.config.AliyunOssConfig;
import com.jycz.qingyun.model.dto.AssignmentCreateRequest;
import com.jycz.qingyun.model.dto.AssignmentGradeRequest;
import com.jycz.qingyun.model.dto.AssignmentSubmitRequest;
import com.jycz.qingyun.model.entity.*;
import com.jycz.qingyun.model.vo.*;
import com.jycz.qingyun.mapper.*;
import com.jycz.qingyun.service.AIService;
import com.jycz.qingyun.service.AssignmentService;
import com.jycz.qingyun.service.NoticeService;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {
//
    private final AssignmentMapper assignmentMapper;
    private final QuestionMapper questionMapper;
    private final ObjectSubmitMapper objectSubmitMapper;
    private final SubjectSubmitMapper subjectSubmitMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final CourseMapper courseMapper;  // ← 新增
    private final NoticeService noticeService;  // ← 新增
    private final PointsRecordService pointsRecordService;  // ← 新增
    private final AliyunOssConfig aliyunOssConfig;
    private final RecommendationMapper recommendationMapper;  // ← 新增
    private final AIService aiService;
    @Override
    @Transactional
    public AssignmentCreateVO createAssignment(AssignmentCreateRequest request, Long teacherId) {
        Course course = courseMapper.selectById(request.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 只有 active 状态的课程才能发布作业
        if (!"active".equals(course.getStatus())) {
            throw new BusinessException(400, "课程当前不可用，无法发布作业（课程状态：pending 或 archived）");
        }

        // 校验教师权限
        if (!course.getUserId().equals(teacherId)) {
            throw new BusinessException(403, "您不是该课程的教师，无权发布作业");
        }
        int totalScore = request.getQuestions().stream()
                .mapToInt(AssignmentCreateRequest.QuestionRequest::getPerscore)
                .sum();

        Assignment assignment = new Assignment();
        assignment.setCourseId(request.getCourseId());
        assignment.setAssignmentTitle(request.getAssignmentTitle());
        assignment.setDeadline(request.getDeadline());
        assignment.setMaxScore(totalScore);

        assignment.setAssignmentCreateTime(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        assignmentMapper.insert(assignment);

        int sortOrder = 1;
        for (AssignmentCreateRequest.QuestionRequest qr : request.getQuestions()) {
            Question question = new Question();
            question.setAssignmentId(assignment.getId());
            question.setType(qr.getType());
            question.setStem(qr.getStem());

            if (qr.getOptions() != null && !qr.getOptions().isEmpty()) {
                try {
                    String optionsJson = objectMapper.writeValueAsString(qr.getOptions());
                    question.setOptions(optionsJson);
                } catch (Exception e) {
                    throw new BusinessException(500, "选项格式错误");
                }
            }

            question.setAnswer(qr.getAnswer());
            question.setExplanation(qr.getExplanation());
            question.setPerscore(qr.getPerscore());
            question.setSortOrder(sortOrder++);
            question.setImageUrl(resolveOssKey(qr.getImageUrl()));
            questionMapper.insert(question);
        }

        log.info("作业发布成功: assignmentId={}, teacherId={}", assignment.getId(), teacherId);
        // ✅ 发送作业发布通知给所有学生
        noticeService.sendAssignmentNotice(
                request.getCourseId(),
                request.getAssignmentTitle(),
                request.getDeadline().toString()
        );
        return AssignmentCreateVO.builder()
                .assignmentId(assignment.getId())
                .courseId(assignment.getCourseId())
                .assignmentTitle(assignment.getAssignmentTitle())
                .deadline(assignment.getDeadline())
                .maxScore(assignment.getMaxScore())
                .questionCount(request.getQuestions().size())
                .createdAt(assignment.getAssignmentCreateTime())
                .build();
    }

    @Override
    public List<AssignmentStudentListVO> getStudentAssignmentList(Long studentId) {
        // 1. 查询该学生加入的所有课程
        LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CourseStudent::getUserId, studentId);
        List<CourseStudent> courseStudents = courseStudentMapper.selectList(csWrapper);

        if (courseStudents.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 获取课程ID列表
        List<Long> courseIds = courseStudents.stream()
                .map(CourseStudent::getCourseId)
                .collect(Collectors.toList());

        // 3. 查询这些课程下的所有作业
        LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Assignment::getCourseId, courseIds)
                .orderByDesc(Assignment::getAssignmentCreateTime);
        List<Assignment> assignments = assignmentMapper.selectList(wrapper);

        if (assignments.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 批量查询课程信息（获取课程名称）
        Map<Long, Course> courseMap = courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        // 5. 组装数据
        List<AssignmentStudentListVO> result = new ArrayList<>();

        for (Assignment assignment : assignments) {
            // 查询该学生对这份作业的提交情况
            List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectByAssignmentAndUser(assignment.getId(), studentId);
            List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectByAssignmentAndUser(assignment.getId(), studentId);

            boolean hasSubmitted = !objectSubmits.isEmpty() || !subjectSubmits.isEmpty();
            String status;
            Integer myScore = null;

            if (hasSubmitted) {
                boolean allGraded = true;
                int totalScore = 0;
                for (SubjectSubmit ss : subjectSubmits) {
                    if (ss.getGradingStatus() == 1) {
                        allGraded = false;
                        break;
                    }
                    totalScore += ss.getSubjectScore() != null ? ss.getSubjectScore() : 0;
                }
                for (ObjectSubmit os : objectSubmits) {
                    totalScore += os.getObjectScore() != null ? os.getObjectScore() : 0;
                }

                // ✅ 修改：只要所有提交都已批改（没有待批改的主观题），就显示 GRADED
                if (allGraded) {
                    status = "GRADED";
                    myScore = totalScore;
                } else {
                    status = "SUBMITTED";
                }
            } else if (LocalDateTime.now().isAfter(assignment.getDeadline())) {
                status = "OVERDUE";
            } else {
                status = "PENDING";
            }

            // 获取课程名称
            Course course = courseMap.get(assignment.getCourseId());
            String courseName = course != null ? course.getCourseTitle() : "未知课程";

            result.add(AssignmentStudentListVO.builder()
                    .assignmentId(assignment.getId())
                    .courseId(assignment.getCourseId())
                    .courseName(courseName)
                    .assignmentTitle(assignment.getAssignmentTitle())
                    .deadline(assignment.getDeadline())
                    .maxScore(assignment.getMaxScore())
                    .status(status)
                    .myScore(myScore)
                    .createdAt(assignment.getAssignmentCreateTime())
                    .build());
        }

        return result;
    }

    @Override
    public AssignmentDetailVO getAssignmentDetail(Long assignmentId, Long userId) {
        // 1. 查询作业
        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException(404, "作业不存在");
        }

        // 2. 查询题目列表
        LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.eq(Question::getAssignmentId, assignmentId)
                .orderByAsc(Question::getSortOrder);
        List<Question> questions = questionMapper.selectList(qWrapper);

        // 3. 查询提交记录
        List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectByAssignmentAndUser(assignmentId, userId);
        List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectByAssignmentAndUser(assignmentId, userId);

        // 4. 处理重复 key，保留最新的
        Map<Long, ObjectSubmit> objectSubmitMap = objectSubmits.stream()
                .collect(Collectors.toMap(
                        ObjectSubmit::getQuestionId,
                        o -> o,
                        (existing, replacement) -> replacement
                ));

        Map<Long, SubjectSubmit> subjectSubmitMap = subjectSubmits.stream()
                .collect(Collectors.toMap(
                        SubjectSubmit::getQuestionId,
                        s -> s,
                        (existing, replacement) -> replacement
                ));

        // 5. 组装题目详情
        List<AssignmentDetailVO.QuestionDetailVO> questionDetails = new ArrayList<>();
        int totalAutoScore = 0;
        int totalSubjectiveScore = 0;
        boolean allGraded = true;

        // ✅ 收集错题用于薄弱知识点分析
        List<Question> wrongQuestions = new ArrayList<>();
        List<Question> allQuestions = questions;

        for (Question q : questions) {
            // 解析 options
            List<String> optionsList = new ArrayList<>();
            if (q.getType() == 1 || q.getType() == 2) {
                if (q.getOptions() != null && !q.getOptions().isEmpty()) {
                    try {
                        optionsList = objectMapper.readValue(q.getOptions(), new TypeReference<List<String>>() {});
                    } catch (Exception e) {
                        log.warn("解析选项失败: {}", e.getMessage());
                        optionsList = new ArrayList<>();
                    }
                }
            }

            AssignmentDetailVO.QuestionDetailVO.QuestionDetailVOBuilder builder =
                    AssignmentDetailVO.QuestionDetailVO.builder()
                            .questionId(q.getId())
                            .type(q.getType())
                            .stem(q.getStem())
                            .perscore(q.getPerscore())
                            .sortOrder(q.getSortOrder())
                            .imageUrl(q.getImageUrl())
                            .explanation(q.getExplanation())
                            .options(optionsList);

            if (q.getType() != 5) {
                // 客观题
                ObjectSubmit os = objectSubmitMap.get(q.getId());
                if (os != null) {
                    boolean isCorrect = q.getAnswer() != null && q.getAnswer().equals(os.getAnswerWord());
                    builder.myAnswer(os.getAnswerWord());
                    builder.isCorrect(isCorrect);
                    builder.score(os.getObjectScore());
                    totalAutoScore += os.getObjectScore() != null ? os.getObjectScore() : 0;

                    // ✅ 如果是错题，加入错题列表
                    if (!isCorrect) {
                        wrongQuestions.add(q);
                    }
                }
            } else {
                // 主观题
                SubjectSubmit ss = subjectSubmitMap.get(q.getId());
                if (ss != null) {
                    builder.myAnswer(ss.getAnswerPicture());
                    if (ss.getGradingStatus() == 2) {
                        builder.score(ss.getSubjectScore());
                        builder.teacherComment(ss.getTeacherComment());
                        totalSubjectiveScore += ss.getSubjectScore() != null ? ss.getSubjectScore() : 0;
                    } else {
                        allGraded = false;
                    }
                }
            }

            // ✅ 设置知识点（预留字段，由 AI 分析后填充）
            builder.knowledgePoint(null);

            questionDetails.add(builder.build());
        }

        // 6. 计算作业状态
        boolean hasSubmitted = !objectSubmits.isEmpty() || !subjectSubmits.isEmpty();
        String status;
        if (!hasSubmitted && LocalDateTime.now().isAfter(assignment.getDeadline())) {
            status = "OVERDUE";
        } else if (!hasSubmitted) {
            status = "PENDING";
        } else if (allGraded && !subjectSubmits.isEmpty()) {
            status = "GRADED";
        } else {
            status = "SUBMITTED";
        }

        Integer totalScore = null;
        if (allGraded && !subjectSubmits.isEmpty()) {
            totalScore = totalAutoScore + totalSubjectiveScore;
        }

        // 7. ✅ 生成薄弱知识点（仅当有错题时）
        List<AssignmentDetailVO.WeakPointVO> weakPoints = new ArrayList<>();
        if (!wrongQuestions.isEmpty()) {
            try {
                // 获取正确题目用于对比
                List<Question> correctQuestions = allQuestions.stream()
                        .filter(q -> q.getType() != 5 && !wrongQuestions.contains(q))
                        .collect(Collectors.toList());

                List<Map<String, Object>> weakPointMaps = aiService.analyzeWeakPoints(wrongQuestions, correctQuestions);
                for (Map<String, Object> map : weakPointMaps) {
                    weakPoints.add(AssignmentDetailVO.WeakPointVO.builder()
                            .knowledgePoint((String) map.get("knowledgePoint"))
                            .explanation((String) map.get("explanation"))
                            .wrongCount((Integer) map.getOrDefault("wrongCount", 0))
                            .wrongQuestions((List<String>) map.getOrDefault("wrongQuestions", new ArrayList<>()))
                            .build());
                }
            } catch (Exception e) {
                log.error("作业详情-分析薄弱知识点失败: {}", e.getMessage());
            }
        }

        // 8. 返回结果
        return AssignmentDetailVO.builder()
                .assignmentId(assignment.getId())
                .courseId(assignment.getCourseId())
                .assignmentTitle(assignment.getAssignmentTitle())
                .deadline(assignment.getDeadline())
                .maxScore(assignment.getMaxScore())
                .status(status)
                .autoScore(totalAutoScore)
                .subjectiveScore(totalSubjectiveScore)
                .totalScore(totalScore)
                .questions(questionDetails)
                .weakPoints(weakPoints)  // ← 新增薄弱知识点
                .build();
    }

    @Override
    @Transactional
    public AssignmentSubmitVO submitAssignment(AssignmentSubmitRequest request, Long studentId) {
        // 1. 查询作业
        Assignment assignment = assignmentMapper.selectById(request.getAssignmentId());
        if (assignment == null) {
            throw new BusinessException(404, "作业不存在");
        }

        // 2. 校验截止时间
        if (LocalDateTime.now().isAfter(assignment.getDeadline())) {
            throw new BusinessException(400, "已超过截止时间，无法提交");
        }

        // 3. ✅ 防重复提交校验
        LambdaQueryWrapper<ObjectSubmit> objWrapper = new LambdaQueryWrapper<>();
        objWrapper.eq(ObjectSubmit::getAssignmentId, request.getAssignmentId())
                .eq(ObjectSubmit::getUserId, studentId);
        long objCount = objectSubmitMapper.selectCount(objWrapper);

        LambdaQueryWrapper<SubjectSubmit> subWrapper = new LambdaQueryWrapper<>();
        subWrapper.eq(SubjectSubmit::getAssignmentId, request.getAssignmentId())
                .eq(SubjectSubmit::getUserId, studentId);
        long subCount = subjectSubmitMapper.selectCount(subWrapper);

        if (objCount > 0 || subCount > 0) {
            throw new BusinessException(409, "您已提交过该作业，请勿重复提交");
        }

        // 4. 查询题目列表
        LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.eq(Question::getAssignmentId, request.getAssignmentId())
                .orderByAsc(Question::getSortOrder);
        List<Question> questions = questionMapper.selectList(qWrapper);

        if (questions.isEmpty()) {
            throw new BusinessException(400, "该作业没有题目，无法提交");
        }

        // 5. 建立 sortOrder -> Question 映射
        Map<Integer, Question> questionMap = new HashMap<>();
        for (Question q : questions) {
            if (q.getSortOrder() != null) {
                questionMap.put(q.getSortOrder(), q);
            }
        }

        // 6. 处理提交
        int totalAutoScore = 0;
        for (AssignmentSubmitRequest.AnswerRequest answer : request.getAnswers()) {
            Question q = questionMap.get(answer.getSortOrder());
            if (q == null) {
                throw new BusinessException(400, "题目序号 " + answer.getSortOrder() + " 不存在");
            }

            if (q.getType() == 5) {
                // 主观题
                SubjectSubmit ss = new SubjectSubmit();
                ss.setAssignmentId(request.getAssignmentId());
                ss.setUserId(studentId);
                ss.setQuestionId(q.getId());
                ss.setAnswerPicture(answer.getAnswer());
                ss.setFinishStatus(2);
                ss.setGradingStatus(1);
                ss.setFinishTime(LocalDateTime.now());
                subjectSubmitMapper.insert(ss);
            } else {
                // 客观题
                int score = 0;
                if (q.getAnswer() != null && q.getAnswer().equals(answer.getAnswer())) {
                    score = q.getPerscore();
                    totalAutoScore += score;
                }

                ObjectSubmit os = new ObjectSubmit();
                os.setAssignmentId(request.getAssignmentId());
                os.setQuestionId(q.getId());
                os.setUserId(studentId);
                os.setObjectScore(score);
                os.setAnswerWord(answer.getAnswer());
                os.setSubmitTime(LocalDateTime.now());
                objectSubmitMapper.insert(os);
            }
        }

        log.info("作业提交成功: assignmentId={}, studentId={}, autoScore={}",
                request.getAssignmentId(), studentId, totalAutoScore);

        // 7. 发送提交作业通知给教师
        Course course = courseMapper.selectById(assignment.getCourseId());
        if (course != null) {
            User student = userMapper.selectById(studentId);
            String studentName = student != null ? student.getName() : "未知学生";
            noticeService.sendSubmitNotice(course.getUserId(), studentName, assignment.getAssignmentTitle());
        }

        // 8. ✅ 生成薄弱知识点分析
        List<AssignmentSubmitVO.WeakPointVO> weakPoints = analyzeWeakPoints(assignment, studentId);

        // 9. ✅ 异步生成智能推荐
        generateRecommendationAsync(assignment, studentId);

        return AssignmentSubmitVO.builder()
                .assignmentId(request.getAssignmentId())
                .status("SUBMITTED")
                .submitTime(LocalDateTime.now())
                .autoScore(totalAutoScore)
                .maxScore(assignment.getMaxScore())
                .subjectivePending(true)
                .weakPoints(weakPoints)  // ← 新增
                .build();
    }

    // ========== ✅ 新增：分析薄弱知识点 ==========
    private List<AssignmentSubmitVO.WeakPointVO> analyzeWeakPoints(Assignment assignment, Long studentId) {
        try {
            // 1. 获取该学生该作业的提交记录
            List<ObjectSubmit> submits = objectSubmitMapper.selectByAssignmentAndUser(assignment.getId(), studentId);
            List<Question> allQuestions = questionMapper.selectList(
                    new LambdaQueryWrapper<Question>()
                            .eq(Question::getAssignmentId, assignment.getId())
            );

            // 2. 区分错题和正确题
            List<Question> wrongQuestions = new ArrayList<>();
            List<Question> correctQuestions = new ArrayList<>();
            Map<Long, ObjectSubmit> submitMap = submits.stream()
                    .collect(Collectors.toMap(ObjectSubmit::getQuestionId, s -> s));

            for (Question q : allQuestions) {
                if (q.getType() == 5) continue; // 跳过主观题
                ObjectSubmit submit = submitMap.get(q.getId());
                if (submit != null) {
                    if (q.getAnswer() != null && !q.getAnswer().equals(submit.getAnswerWord())) {
                        wrongQuestions.add(q);
                    } else {
                        correctQuestions.add(q);
                    }
                }
            }

            if (wrongQuestions.isEmpty()) {
                return new ArrayList<>();
            }

            // 3. 调用 AI 分析薄弱知识点
            List<Map<String, Object>> weakPointMaps = aiService.analyzeWeakPoints(wrongQuestions, correctQuestions);

            // 4. 转换为 VO
            List<AssignmentSubmitVO.WeakPointVO> result = new ArrayList<>();
            for (Map<String, Object> map : weakPointMaps) {
                result.add(AssignmentSubmitVO.WeakPointVO.builder()
                        .knowledgePoint((String) map.get("knowledgePoint"))
                        .explanation((String) map.get("explanation"))
                        .wrongCount((Integer) map.getOrDefault("wrongCount", 0))
                        .wrongQuestions((List<String>) map.getOrDefault("wrongQuestions", new ArrayList<>()))
                        .build());
            }
            return result;

        } catch (Exception e) {
            log.error("分析薄弱知识点失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public AssignmentGradeVO gradeAssignment(AssignmentGradeRequest request, Long teacherId) {
        Assignment assignment = assignmentMapper.selectById(request.getAssignmentId());
        if (assignment == null) {
            throw new BusinessException(404, "作业不存在");
        }

        // 1. 客观题总分
        List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectByAssignmentAndUser(
                request.getAssignmentId(), request.getStudentId());
        int autoScore = objectSubmits.stream()
                .mapToInt(os -> os.getObjectScore() != null ? os.getObjectScore() : 0)
                .sum();
        int totalScore = autoScore;

        // 2. ✅ 批改主观题（使用 updateSubjectScore）
        for (AssignmentGradeRequest.GradeRequest grade : request.getGrades()) {
            int rows = subjectSubmitMapper.updateSubjectScore(
                    request.getAssignmentId(),
                    request.getStudentId(),
                    grade.getQuestionId(),
                    grade.getScore(),
                    grade.getTeacherComment()
            );
            log.info("批改主观题: questionId={}, rows={}", grade.getQuestionId(), rows);

            if (rows > 0 && grade.getScore() != null) {
                totalScore += grade.getScore();
            }
        }

        log.info("批改完成: assignmentId={}, totalScore={}", request.getAssignmentId(), totalScore);

        User student = userMapper.selectById(request.getStudentId());

        // 发送通知和积分处理
        noticeService.sendGradeNotice(request.getStudentId(), assignment.getAssignmentTitle(), totalScore);
        pointsRecordService.handleAssignmentGradePoints(request.getStudentId(), totalScore);

        return AssignmentGradeVO.builder()
                .assignmentId(request.getAssignmentId())
                .studentId(request.getStudentId())
                .studentName(student != null ? student.getName() : "未知学生")
                .totalScore(totalScore)
                .maxScore(assignment.getMaxScore())
                .gradedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 异步生成智能推荐习题（基于客观题错题）
     */
    @Async
    public void generateRecommendationAsync(Assignment assignment, Long studentId) {
        try {
            // 1. 获取该学生该作业的客观题提交记录
            List<ObjectSubmit> submits = objectSubmitMapper.selectByAssignmentAndUser(
                    assignment.getId(), studentId);

            if (submits.isEmpty()) {
                log.info("学生 {} 作业 {} 无客观题提交记录", studentId, assignment.getId());
                return;
            }

            // 2. 获取作业所有题目
            List<Question> allQuestions = questionMapper.selectList(
                    new LambdaQueryWrapper<Question>()
                            .eq(Question::getAssignmentId, assignment.getId())
            );

            if (allQuestions.isEmpty()) {
                log.info("作业 {} 没有题目", assignment.getId());
                return;
            }

            // 3. 区分错题和正确题
            List<Long> wrongQuestionIds = new ArrayList<>();
            for (ObjectSubmit submit : submits) {
                Question q = questionMapper.selectById(submit.getQuestionId());
                if (q != null && q.getType() != 5) { // 只处理客观题
                    if (q.getAnswer() != null && !q.getAnswer().equals(submit.getAnswerWord())) {
                        wrongQuestionIds.add(q.getId());
                    }
                }
            }

            // 4. 如果没有错题，不生成推荐
            if (wrongQuestionIds.isEmpty()) {
                log.info("学生 {} 作业 {} 全部正确，无需推荐", studentId, assignment.getId());
                return;
            }

            // 5. 获取错题详情
            List<Question> wrongQuestions = questionMapper.selectBatchIds(wrongQuestionIds);
            log.info("学生 {} 作业 {} 有 {} 道错题", studentId, assignment.getId(), wrongQuestions.size());

            // 6. 调用 AI 生成推荐习题
            List<Map<String, Object>> recommendations = aiService.generateExerciseRecommendation(
                    wrongQuestions, allQuestions, 3
            );

            // 7. 检查 AI 返回结果是否为空
            if (recommendations == null || recommendations.isEmpty()) {
                log.warn("AI 推荐习题生成失败或返回为空，使用降级方案");
                // 降级方案：从错题中随机选几道作为推荐
                recommendations = generateFallbackRecommendations(wrongQuestions, 3);
            }

            if (recommendations.isEmpty()) {
                log.warn("降级方案也未能生成推荐习题");
                return;
            }

            // 8. 保存推荐记录
            Recommendation rec = new Recommendation();
            rec.setUserId(studentId);
            rec.setAssignmentId(assignment.getId());
            rec.setQuestions(objectMapper.writeValueAsString(recommendations));
            rec.setStatus(0);
            recommendationMapper.insert(rec);

            log.info("推荐习题生成成功: studentId={}, assignmentId={}, count={}",
                    studentId, assignment.getId(), recommendations.size());

            // 9. 发送通知给学生
            noticeService.addNotice(
                    studentId,
                    "📚 智能推荐习题",
                    "根据你的错题情况，系统为你推荐了 " + recommendations.size() + " 道针对性练习题，快去练习吧！",
                    13
            );

        } catch (Exception e) {
            log.error("生成推荐习题失败: studentId={}, assignmentId={}, error={}",
                    studentId, assignment.getId(), e.getMessage(), e);
        }
    }

    /**
     * 降级方案：从错题中随机选几道作为推荐
     */
    private List<Map<String, Object>> generateFallbackRecommendations(List<Question> wrongQuestions, int count) {
        List<Map<String, Object>> fallback = new ArrayList<>();
        int size = Math.min(count, wrongQuestions.size());
        // 随机打乱
        Collections.shuffle(wrongQuestions);
        for (int i = 0; i < size; i++) {
            Question q = wrongQuestions.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("stem", q.getStem());
            item.put("type", q.getType());
            item.put("options", new ArrayList<>());
            item.put("answer", q.getAnswer());
            item.put("explanation", q.getExplanation() != null ? q.getExplanation() : "请复习相关知识点");
            item.put("knowledgePoint", "需要巩固的知识点");
            fallback.add(item);
        }
        return fallback;
    }

    @Async
    public void generateRecommendationFromCommentsAsync(AssignmentGradeRequest request, Assignment assignment) {
        try {
            // 1. 获取主观题提交和评语
            List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectList(
                    new LambdaQueryWrapper<SubjectSubmit>()
                            .eq(SubjectSubmit::getAssignmentId, request.getAssignmentId())
                            .eq(SubjectSubmit::getUserId, request.getStudentId())
                            .eq(SubjectSubmit::getGradingStatus, 2)
            );

            if (subjectSubmits.isEmpty()) return;

            // 2. 提取答案和评语
            List<String> answers = new ArrayList<>();
            List<String> comments = new ArrayList<>();
            for (SubjectSubmit ss : subjectSubmits) {
                answers.add(ss.getAnswerPicture() != null ? ss.getAnswerPicture() : "[图片]");
                comments.add(ss.getTeacherComment() != null ? ss.getTeacherComment() : "");
            }

            // 3. 调用 AI 生成推荐
            List<Map<String, Object>> recommendations = aiService.generateRecommendationFromTeacherComments(
                    assignment.getAssignmentTitle(), answers, comments, 3
            );

            if (!recommendations.isEmpty()) {
                Recommendation rec = new Recommendation();
                rec.setUserId(request.getStudentId());
                rec.setAssignmentId(assignment.getId());
                rec.setQuestions(objectMapper.writeValueAsString(recommendations));
                rec.setStatus(0);
                recommendationMapper.insert(rec);

                noticeService.addNotice(
                        request.getStudentId(),
                        "📚 智能推荐习题",
                        "老师已批改完你的主观题，系统为你推荐了 " + recommendations.size() + " 道针对性练习题！",
                        13
                );
            }
        } catch (Exception e) {
            log.error("基于老师评语生成推荐失败: {}", e.getMessage());
        }
    }

    @Override
    public List<AssignmentTeacherListVO> getTeacherAssignmentList(Long courseId, Long teacherId) {
        LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Assignment::getCourseId, courseId)
                .orderByDesc(Assignment::getAssignmentCreateTime);
        List<Assignment> assignments = assignmentMapper.selectList(wrapper);

        int totalStudents = courseStudentMapper.countByCourseId(courseId);


        List<AssignmentTeacherListVO> result = new ArrayList<>();
        for (Assignment assignment : assignments) {
            List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectList(
                    new LambdaQueryWrapper<ObjectSubmit>()
                            .eq(ObjectSubmit::getAssignmentId, assignment.getId())
            );
            List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectList(
                    new LambdaQueryWrapper<SubjectSubmit>()
                            .eq(SubjectSubmit::getAssignmentId, assignment.getId())
            );

            Set<Long> submittedUserIds = new HashSet<>();
            objectSubmits.forEach(o -> submittedUserIds.add(o.getUserId()));
            subjectSubmits.forEach(s -> submittedUserIds.add(s.getUserId()));

            int submitCount = submittedUserIds.size();
            double submissionRate = totalStudents > 0 ?
                    Math.round((double) submitCount / totalStudents * 100 * 100) / 100.0 : 0.0;

            double avgScore = 0.0;
            int gradedCount = 0;
            int totalScoreSum = 0;

            if (submitCount > 0) {
                for (Long studentId : submittedUserIds) {
                    int objScore = objectSubmits.stream()
                            .filter(o -> o.getUserId().equals(studentId))
                            .mapToInt(o -> o.getObjectScore() != null ? o.getObjectScore() : 0)
                            .sum();

                    int subScore = subjectSubmits.stream()
                            .filter(s -> s.getUserId().equals(studentId) && s.getGradingStatus() == 2)
                            .mapToInt(s -> s.getSubjectScore() != null ? s.getSubjectScore() : 0)
                            .sum();

                    boolean hasGradedSubject = subjectSubmits.stream()
                            .anyMatch(s -> s.getUserId().equals(studentId) && s.getGradingStatus() == 2);

                    if (hasGradedSubject) {
                        totalScoreSum += objScore + subScore;
                        gradedCount++;
                    }
                }
            }

            avgScore = gradedCount > 0 ?
                    Math.round((double) totalScoreSum / gradedCount * 100) / 100.0 : 0.0;

            result.add(AssignmentTeacherListVO.builder()
                    .assignmentId(assignment.getId())
                    .assignmentTitle(assignment.getAssignmentTitle())
                    .deadline(assignment.getDeadline())
                    .maxScore(assignment.getMaxScore())
                    .submitCount(submitCount)
                    .totalStudents(totalStudents)
                    .submissionRate(submissionRate)
                    .avgScore(avgScore)
                    .createdAt(assignment.getAssignmentCreateTime())
                    .build());
        }

        return result;
    }

    // ========== 新增：老师查看具体学生作业提交情况 ==========
    @Override
    public AssignmentStudentGradeVO getStudentGrades(Long assignmentId, Long teacherId) {
        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException(404, "作业不存在");
        }

        Course course = courseMapper.selectById(assignment.getCourseId());
        if (course == null || !course.getUserId().equals(teacherId)) {
            throw new BusinessException(403, "您不是该课程的老师");
        }

        List<CourseStudent> courseStudents = courseStudentMapper.selectList(
                new LambdaQueryWrapper<CourseStudent>()
                        .eq(CourseStudent::getCourseId, assignment.getCourseId())
        );

        List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectList(
                new LambdaQueryWrapper<ObjectSubmit>()
                        .eq(ObjectSubmit::getAssignmentId, assignmentId)
        );
        List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectList(
                new LambdaQueryWrapper<SubjectSubmit>()
                        .eq(SubjectSubmit::getAssignmentId, assignmentId)
        );

        Map<Long, Integer> objectScoreMap = objectSubmits.stream()
                .collect(Collectors.toMap(ObjectSubmit::getUserId,
                        o -> o.getObjectScore() != null ? o.getObjectScore() : 0,
                        (a, b) -> a));

        Map<Long, Integer> subjectScoreMap = subjectSubmits.stream()
                .filter(s -> s.getGradingStatus() == 2)
                .collect(Collectors.toMap(SubjectSubmit::getUserId,
                        s -> s.getSubjectScore() != null ? s.getSubjectScore() : 0,
                        (a, b) -> a));

        Map<Long, Boolean> gradedMap = subjectSubmits.stream()
                .collect(Collectors.toMap(SubjectSubmit::getUserId,
                        s -> s.getGradingStatus() == 2,
                        (a, b) -> a || b));

        Set<Long> submittedUserIds = new HashSet<>();
        objectSubmits.forEach(o -> submittedUserIds.add(o.getUserId()));
        subjectSubmits.forEach(s -> submittedUserIds.add(s.getUserId()));

        List<AssignmentStudentGradeVO.StudentGradeVO> list = new ArrayList<>();
        for (CourseStudent cs : courseStudents) {
            Long studentId = cs.getUserId();
            User student = userMapper.selectById(studentId);

            boolean isSubmitted = submittedUserIds.contains(studentId);
            boolean isGraded = gradedMap.getOrDefault(studentId, false);

            String status;
            if (!isSubmitted) {
                status = LocalDateTime.now().isAfter(assignment.getDeadline()) ? "OVERDUE" : "PENDING";
            } else if (isGraded) {
                status = "GRADED";
            } else {
                status = "SUBMITTED";
            }

            int objectScore = objectScoreMap.getOrDefault(studentId, 0);
            int subjectScore = subjectScoreMap.getOrDefault(studentId, 0);
            int totalScore = objectScore + subjectScore;

            list.add(AssignmentStudentGradeVO.StudentGradeVO.builder()
                    .studentId(studentId)
                    .studentName(student != null ? student.getName() : "未知学生")
                    .status(status)
                    .score(isGraded ? totalScore : null)
                    .objectScore(isSubmitted ? objectScore : null)
                    .subjectScore(isGraded ? subjectScore : null)
                    .isGraded(isGraded)
                    .build());
        }

        return AssignmentStudentGradeVO.builder()
                .assignmentId(assignment.getId())
                .assignmentTitle(assignment.getAssignmentTitle())
                .maxScore(assignment.getMaxScore())
                .list(list)
                .build();
    }

    @Override
    public List<PendingAssignmentVO> getPendingAssignments(Long courseId, Long studentId, Long teacherId) {
        // 1. 校验教师是否有权限（是该课程的教师）
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }
        if (!course.getUserId().equals(teacherId)) {
            throw new BusinessException(403, "您不是该课程的教师");
        }

        // 2. 查询该课程下所有作业
        LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Assignment::getCourseId, courseId)
                .orderByDesc(Assignment::getAssignmentCreateTime);
        List<Assignment> assignments = assignmentMapper.selectList(wrapper);

        if (assignments.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> assignmentIds = assignments.stream()
                .map(Assignment::getId)
                .collect(Collectors.toList());

        // 3. 查询该学生所有主观题提交记录（待批改：grading_status = 1）
        List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectList(
                new LambdaQueryWrapper<SubjectSubmit>()
                        .in(SubjectSubmit::getAssignmentId, assignmentIds)
                        .eq(SubjectSubmit::getUserId, studentId)
                        .eq(SubjectSubmit::getGradingStatus, 1)  // 待批改
        );

        if (subjectSubmits.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 按作业分组
        Map<Long, List<SubjectSubmit>> submitMap = subjectSubmits.stream()
                .collect(Collectors.groupingBy(SubjectSubmit::getAssignmentId));

        // 5. 查询题目信息
        List<Long> questionIds = subjectSubmits.stream()
                .map(SubjectSubmit::getQuestionId)
                .collect(Collectors.toList());
        Map<Long, Question> questionMap = questionMapper.selectBatchIds(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        // 6. 组装结果
        List<PendingAssignmentVO> result = new ArrayList<>();

        for (Assignment assignment : assignments) {
            List<SubjectSubmit> submits = submitMap.get(assignment.getId());
            if (submits == null || submits.isEmpty()) {
                continue;
            }

            // 获取该作业的所有待批改主观题
            List<PendingAssignmentVO.SubjectiveQuestionVO> questions = new ArrayList<>();
            for (SubjectSubmit ss : submits) {
                Question q = questionMap.get(ss.getQuestionId());
                if (q != null) {
                    questions.add(PendingAssignmentVO.SubjectiveQuestionVO.builder()

                            .sortOrder(q.getSortOrder())
                            .stem(q.getStem())
                            .perscore(q.getPerscore())
                            .answerPicture(ss.getAnswerPicture())
                            .gradingStatus(ss.getGradingStatus())
                            .build());
                }
            }

            if (questions.isEmpty()) {
                continue;
            }

            // 获取学生提交时间（取第一个主观题提交时间）
            LocalDateTime submitTime = submits.get(0).getFinishTime();

            result.add(PendingAssignmentVO.builder()
                    .assignmentId(assignment.getId())
                    .assignmentTitle(assignment.getAssignmentTitle())
                    .courseId(assignment.getCourseId())
                    .courseName(course.getCourseTitle())
                    .deadline(assignment.getDeadline())
                    .maxScore(assignment.getMaxScore())
                    .submitTime(submitTime)
                    .subjectiveQuestions(questions)
                    .build());
        }

        return result;
    }

    /**
     * 从 OSS 签名 URL 中提取文件 key（路径），非 OSS URL 原样返回
     * 新数据只存 key，老数据兼容完整签名 URL
     */
    private String resolveOssKey(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        // 已是 key（非 URL），直接返回
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            return imageUrl;
        }
        // 完整 URL → 提取 key
        try {
            URL url = new URL(imageUrl);
            String host = url.getHost();
            if (host != null && host.contains(aliyunOssConfig.getBucket() + ".")) {
                String path = url.getPath();
                return path.startsWith("/") ? path.substring(1) : path;
            }
        } catch (Exception e) {
            log.warn("解析 OSS URL 失败: {}", imageUrl);
        }
        return imageUrl;
    }
}