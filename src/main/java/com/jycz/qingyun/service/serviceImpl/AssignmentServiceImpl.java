package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.model.dto.AssignmentCreateRequest;
import com.jycz.qingyun.model.dto.AssignmentGradeRequest;
import com.jycz.qingyun.model.dto.AssignmentSubmitRequest;
import com.jycz.qingyun.model.entity.*;
import com.jycz.qingyun.model.vo.*;
import com.jycz.qingyun.mapper.*;
import com.jycz.qingyun.service.AssignmentService;
import com.jycz.qingyun.service.NoticeService;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
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

        Assignment assignment = new Assignment();
        assignment.setCourseId(request.getCourseId());
        assignment.setAssignmentTitle(request.getAssignmentTitle());
        assignment.setDeadline(request.getDeadline());
        assignment.setMaxScore(request.getMaxScore());
        assignment.setStudentStatus("PENDING");
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
            question.setImageUrl(qr.getImageUrl());
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
        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException(404, "作业不存在");
        }

        LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.eq(Question::getAssignmentId, assignmentId)
                .orderByAsc(Question::getSortOrder);
        List<Question> questions = questionMapper.selectList(qWrapper);

        List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectByAssignmentAndUser(assignmentId, userId);
        List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectByAssignmentAndUser(assignmentId, userId);

        Map<Long, ObjectSubmit> objectSubmitMap = objectSubmits.stream()
                .collect(Collectors.toMap(ObjectSubmit::getQuestionId, o -> o));
        Map<Long, SubjectSubmit> subjectSubmitMap = subjectSubmits.stream()
                .collect(Collectors.toMap(SubjectSubmit::getQuestionId, s -> s));

        List<AssignmentDetailVO.QuestionDetailVO> questionDetails = new ArrayList<>();
        int totalAutoScore = 0;
        int totalSubjectiveScore = 0;
        boolean allGraded = true;

        for (Question q : questions) {
            // ========== 新增：解析 options ==========
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
            // ========== 新增结束 ==========

            AssignmentDetailVO.QuestionDetailVO.QuestionDetailVOBuilder builder =
                    AssignmentDetailVO.QuestionDetailVO.builder()

                            .type(q.getType())
                            .stem(q.getStem())
                            .perscore(q.getPerscore())
                            .sortOrder(q.getSortOrder())
                            .imageUrl(q.getImageUrl())
                            .explanation(q.getExplanation())
                            .options(optionsList);  // ← 使用解析出来的选项

            if (q.getType() != 5) {
                ObjectSubmit os = objectSubmitMap.get(q.getId());
                if (os != null) {
                    builder.myAnswer(os.getAnswerWord());
                    builder.isCorrect(q.getAnswer() != null && q.getAnswer().equals(os.getAnswerWord()));
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

        LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.eq(Question::getAssignmentId, request.getAssignmentId())
                .orderByAsc(Question::getSortOrder);
        List<Question> questions = questionMapper.selectList(qWrapper);

        if (questions.isEmpty()) {
            throw new BusinessException(400, "该作业没有题目，无法提交");
        }

        // ✅ 用循环建立映射（避免 null key 问题）
        Map<Integer, Question> questionMap = new HashMap<>();
        for (Question q : questions) {
            if (q.getSortOrder() != null) {
                questionMap.put(q.getSortOrder(), q);
            } else {
                log.warn("题目 {} 的 sortOrder 为 null", q.getId());
            }
        }
        log.info("questionMap keys: {}", questionMap.keySet());

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

        Course course = courseMapper.selectById(assignment.getCourseId());
        if (course != null) {
            User student = userMapper.selectById(studentId);
            String studentName = student != null ? student.getName() : "未知学生";
            noticeService.sendSubmitNotice(course.getUserId(), studentName, assignment.getAssignmentTitle());
        }

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

        List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectByAssignmentAndUser(
                request.getAssignmentId(), request.getStudentId());
        int autoScore = objectSubmits.stream()
                .mapToInt(os -> os.getObjectScore() != null ? os.getObjectScore() : 0)
                .sum();

        int totalScore = autoScore;
        for (AssignmentGradeRequest.GradeRequest grade : request.getGrades()) {
            SubjectSubmit ss = subjectSubmitMapper.selectOne(
                    new LambdaQueryWrapper<SubjectSubmit>()
                            .eq(SubjectSubmit::getAssignmentId, request.getAssignmentId())
                            .eq(SubjectSubmit::getUserId, request.getStudentId())
                            .eq(SubjectSubmit::getQuestionId, grade.getQuestionId())
            );

            if (ss != null) {
                ss.setSubjectScore(grade.getScore());
                ss.setTeacherComment(grade.getTeacherComment());
                ss.setGradingStatus(2);
                ss.setGradingTime(LocalDateTime.now());
                subjectSubmitMapper.updateById(ss);
                totalScore += grade.getScore();
            }
        }

        log.info("批改完成: assignmentId={}, studentId={}, totalScore={}",
                request.getAssignmentId(), request.getStudentId(), totalScore);

        User student = userMapper.selectById(request.getStudentId());
        // ✅ 发送作业批改通知给学生
        noticeService.sendGradeNotice(request.getStudentId(), assignment.getAssignmentTitle(), totalScore);
        // 作业批改积分（成绩 ÷ 5）
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
}