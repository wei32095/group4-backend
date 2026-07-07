package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.jycz.qingyun.service.AsyncAnalysisService;
import com.jycz.qingyun.service.AssignmentService;
import com.jycz.qingyun.service.NoticeService;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentMapper assignmentMapper;
    private final QuestionMapper questionMapper;
    private final ObjectSubmitMapper objectSubmitMapper;
    private final SubjectSubmitMapper subjectSubmitMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final CourseMapper courseMapper;
    private final NoticeService noticeService;
    private final PointsRecordService pointsRecordService;
    private final AliyunOssConfig aliyunOssConfig;
    private final RecommendationMapper recommendationMapper;
    private final AIService aiService;
    private final AsyncAnalysisService asyncAnalysisService;
    private final AssignmentWeakPointsMapper assignmentWeakPointsMapper;
    @Override
    @Transactional
    public AssignmentCreateVO createAssignment(AssignmentCreateRequest request, Long teacherId) {
        Course course = courseMapper.selectById(request.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        if (!"active".equals(course.getStatus())) {
            throw new BusinessException(400, "课程当前不可用，无法发布作业（课程状态：pending 或 archived）");
        }

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
        LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CourseStudent::getUserId, studentId);
        List<CourseStudent> courseStudents = courseStudentMapper.selectList(csWrapper);

        if (courseStudents.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> courseIds = courseStudents.stream()
                .map(CourseStudent::getCourseId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Assignment::getCourseId, courseIds)
                .orderByDesc(Assignment::getAssignmentCreateTime);
        List<Assignment> assignments = assignmentMapper.selectList(wrapper);

        if (assignments.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Course> courseMap = courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        List<AssignmentStudentListVO> result = new ArrayList<>();

        for (Assignment assignment : assignments) {
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

        // 4. 处理重复 key
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

        for (Question q : questions) {
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
                ObjectSubmit os = objectSubmitMap.get(q.getId());
                if (os != null) {
                    boolean isCorrect = q.getAnswer() != null && q.getAnswer().equals(os.getAnswerWord());
                    builder.myAnswer(os.getAnswerWord());
                    builder.isCorrect(isCorrect);
                    builder.score(os.getObjectScore());
                    totalAutoScore += os.getObjectScore() != null ? os.getObjectScore() : 0;
                }
            } else {
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

        // 7. ✅ 读取当前学生的薄弱知识点（从 assignment_weak_points 表）
        List<AssignmentDetailVO.WeakPointVO> weakPoints = new ArrayList<>();
        LambdaQueryWrapper<AssignmentWeakPoints> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssignmentWeakPoints::getAssignmentId, assignmentId)
                .eq(AssignmentWeakPoints::getUserId, userId);
        List<AssignmentWeakPoints> awpList = assignmentWeakPointsMapper.selectList(wrapper);

        if (awpList != null && !awpList.isEmpty()) {
            for (AssignmentWeakPoints awp : awpList) {
                // 解析 wrongQuestions
                List<String> wrongQuestions = new ArrayList<>();
                if (awp.getWrongQuestions() != null && !awp.getWrongQuestions().isEmpty()) {
                    try {
                        wrongQuestions = objectMapper.readValue(
                                awp.getWrongQuestions(),
                                new TypeReference<List<String>>() {}
                        );
                    } catch (Exception e) {
                        log.error("解析错题列表失败: {}", e.getMessage());
                    }
                }

                weakPoints.add(AssignmentDetailVO.WeakPointVO.builder()
                        .knowledgePoint(awp.getKnowledgePoint())
                        .explanation(awp.getExplanation())
                        .wrongCount(awp.getWrongCount())
                        .wrongQuestions(wrongQuestions)
                        .build());
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
                .weakPoints(weakPoints)
                .build();
    }


    @Override
    @Transactional
    public AssignmentSubmitVO submitAssignment(AssignmentSubmitRequest request, Long studentId) {
        Assignment assignment = assignmentMapper.selectById(request.getAssignmentId());
        if (assignment == null) {
            throw new BusinessException(404, "作业不存在");
        }

        if (LocalDateTime.now().isAfter(assignment.getDeadline())) {
            throw new BusinessException(400, "已超过截止时间，无法提交");
        }

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

        LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.eq(Question::getAssignmentId, request.getAssignmentId())
                .orderByAsc(Question::getSortOrder);
        List<Question> questions = questionMapper.selectList(qWrapper);

        if (questions.isEmpty()) {
            throw new BusinessException(400, "该作业没有题目，无法提交");
        }

        Map<Integer, Question> questionMap = new HashMap<>();
        for (Question q : questions) {
            if (q.getSortOrder() != null) {
                questionMap.put(q.getSortOrder(), q);
            }
        }

        int totalAutoScore = 0;
        for (AssignmentSubmitRequest.AnswerRequest answer : request.getAnswers()) {
            Question q = questionMap.get(answer.getSortOrder());
            if (q == null) {
                throw new BusinessException(400, "题目序号 " + answer.getSortOrder() + " 不存在");
            }

            if (q.getType() == 5) {
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

        // 发送提交作业通知给教师
        Course course = courseMapper.selectById(assignment.getCourseId());
        if (course != null) {
            User student = userMapper.selectById(studentId);
            String studentName = student != null ? student.getName() : "未知学生";
            noticeService.sendSubmitNotice(course.getUserId(), studentName, assignment.getAssignmentTitle());
        }

        // ✅ 事务提交后异步生成薄弱知识点分析
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                asyncAnalysisService.generateWeakPointsAsync(assignment, studentId);
            }
        });

        // ✅ 立即返回（没有 weakPoints）
        return AssignmentSubmitVO.builder()
                .assignmentId(request.getAssignmentId())
                .status("SUBMITTED")
                .submitTime(LocalDateTime.now())
                .autoScore(totalAutoScore)
                .maxScore(assignment.getMaxScore())
                .subjectivePending(true)
                .build();
    }

    @Override
    @Transactional
    public AssignmentGradeVO gradeAssignment(AssignmentGradeRequest request, Long teacherId) {
        Assignment assignment = assignmentMapper.selectById(request.getAssignmentId());
        if (assignment == null) {
            throw new BusinessException(404, "作业不存在");
        }

        LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.eq(Question::getAssignmentId, request.getAssignmentId());
        List<Question> questions = questionMapper.selectList(qWrapper);
        Map<Integer, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getSortOrder, q -> q, (a, b) -> a));

        List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectByAssignmentAndUser(
                request.getAssignmentId(), request.getStudentId());
        int autoScore = objectSubmits.stream()
                .mapToInt(os -> os.getObjectScore() != null ? os.getObjectScore() : 0)
                .sum();

        int totalScore = autoScore;

        for (AssignmentGradeRequest.GradeRequest grade : request.getGrades()) {
            // 通过 sortOrder 获取题目
            Question q = questionMap.get(grade.getSortOrder());
            if (q == null) {
                log.warn("未找到题目序号 {} 的题目", grade.getSortOrder());
                continue;
            }

            // 更新主观题批改
            int rows = subjectSubmitMapper.updateSubjectScore(
                    request.getAssignmentId(),
                    request.getStudentId(),
                    q.getId(),  // 用 questionId
                    grade.getScore(),
                    grade.getTeacherComment()
            );
            log.info("批改主观题: sortOrder={}, questionId={}, rows={}",
                    grade.getSortOrder(), q.getId(), rows);

            if (rows > 0 && grade.getScore() != null) {
                totalScore += grade.getScore();
            }
        }

        log.info("批改完成: assignmentId={}, totalScore={}", request.getAssignmentId(), totalScore);

        User student = userMapper.selectById(request.getStudentId());

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

    @Override
    public List<AssignmentTeacherListVO> getTeacherAssignmentList(Long courseId, Long teacherId) {
        LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Assignment::getCourseId, courseId)
                .orderByDesc(Assignment::getAssignmentCreateTime);
        List<Assignment> assignments = assignmentMapper.selectList(wrapper);

        int totalStudents = courseStudentMapper.countByCourseId(courseId);

        List<AssignmentTeacherListVO> result = new ArrayList<>();

        for (Assignment assignment : assignments) {
            // 1. 统计提交人数
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

            // 2. 计算平均分
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

            // 3. ========== 新增：计算每道题的正确率 ==========
            List<AssignmentTeacherListVO.QuestionStatVO> questionStats = new ArrayList<>();

            // 查询该作业的所有题目
            List<Question> questions = questionMapper.selectList(
                    new LambdaQueryWrapper<Question>()
                            .eq(Question::getAssignmentId, assignment.getId())
                            .orderByAsc(Question::getSortOrder)
            );

            for (Question q : questions) {
                // 只统计客观题（type != 5）
                if (q.getType() == 5) continue;

                // 获取该题目的所有提交
                List<ObjectSubmit> submits = objectSubmitMapper.selectList(
                        new LambdaQueryWrapper<ObjectSubmit>()
                                .eq(ObjectSubmit::getQuestionId, q.getId())
                );

                int totalCount = submits.size();
                int correctCount = 0;

                for (ObjectSubmit os : submits) {
                    if (q.getAnswer() != null && q.getAnswer().equals(os.getAnswerWord())) {
                        correctCount++;
                    }
                }

                double correctRate = totalCount > 0 ?
                        Math.round((double) correctCount / totalCount * 100 * 100) / 100.0 : 0.0;

                questionStats.add(AssignmentTeacherListVO.QuestionStatVO.builder()
                        .sortOrder(q.getSortOrder())
                        .correctRate(correctRate)
                        .totalCount(totalCount)
                        .correctCount(correctCount)
                        .build());
            }
            // ========== 新增结束 ==========

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
                    .questionStats(questionStats)  // ← 新增
                    .build());
        }

        return result;
    }

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
    public PendingAssignmentVO getPendingAssignments(Long courseId, Long assignmentId, Long teacherId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }
        if (!course.getUserId().equals(teacherId)) {
            throw new BusinessException(403, "您不是该课程的教师");
        }

        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException(404, "作业不存在");
        }
        if (!assignment.getCourseId().equals(courseId)) {
            throw new BusinessException(400, "作业不属于该课程");
        }

        List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectList(
                new LambdaQueryWrapper<SubjectSubmit>()
                        .eq(SubjectSubmit::getAssignmentId, assignmentId)
                        .eq(SubjectSubmit::getGradingStatus, 1)
                        .orderByAsc(SubjectSubmit::getUserId)
        );

        if (subjectSubmits.isEmpty()) {
            return PendingAssignmentVO.builder()
                    .assignmentId(assignment.getId())
                    .assignmentTitle(assignment.getAssignmentTitle())
                    .courseId(assignment.getCourseId())
                    .courseName(course.getCourseTitle())
                    .deadline(assignment.getDeadline())
                    .maxScore(assignment.getMaxScore())
                    .students(new ArrayList<>())
                    .build();
        }

        Map<Long, List<SubjectSubmit>> submitMap = subjectSubmits.stream()
                .collect(Collectors.groupingBy(SubjectSubmit::getUserId));

        List<Long> questionIds = subjectSubmits.stream()
                .map(SubjectSubmit::getQuestionId)
                .collect(Collectors.toList());
        Map<Long, Question> questionMap = questionMapper.selectBatchIds(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        List<PendingAssignmentVO.StudentPendingVO> students = new ArrayList<>();

        for (Map.Entry<Long, List<SubjectSubmit>> entry : submitMap.entrySet()) {
            Long studentId = entry.getKey();
            List<SubjectSubmit> submits = entry.getValue();

            User student = userMapper.selectById(studentId);
            String studentName = student != null ? student.getName() : "未知学生";

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

            LocalDateTime submitTime = submits.get(0).getFinishTime();

            students.add(PendingAssignmentVO.StudentPendingVO.builder()
                    .studentId(studentId)
                    .studentName(studentName)
                    .submitTime(submitTime)
                    .subjectiveQuestions(questions)
                    .build());
        }

        return PendingAssignmentVO.builder()
                .assignmentId(assignment.getId())
                .assignmentTitle(assignment.getAssignmentTitle())
                .courseId(assignment.getCourseId())
                .courseName(course.getCourseTitle())
                .deadline(assignment.getDeadline())
                .maxScore(assignment.getMaxScore())
                .students(students)
                .build();
    }

    private String resolveOssKey(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            return imageUrl;
        }
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