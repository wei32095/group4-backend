package com.jycz.qingyun.service.serviceImpl;
import com.jycz.qingyun.model.vo.StudyRoomStatisticVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.model.entity.*;
import com.jycz.qingyun.model.vo.StudentAnalysisVO;
import com.jycz.qingyun.mapper.*;
import com.jycz.qingyun.service.AnalysisService;
import com.jycz.qingyun.service.StudyRoomService;
import com.jycz.qingyun.service.PointsRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import com.jycz.qingyun.model.entity.Class;
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final CourseStudentMapper courseStudentMapper;
    private final CourseMapper courseMapper;
    private final ClassMapper classMapper;
    private final ClassCheckMapper classCheckMapper;
    private final AssignmentMapper assignmentMapper;
    private final ObjectSubmitMapper objectSubmitMapper;
    private final SubjectSubmitMapper subjectSubmitMapper;
    private final PointsRecordMapper pointsRecordMapper;
    private final StudyRoomService studyRoomService;
    private final StudentAnalysisMapper studentAnalysisMapper;
    @Override
    public StudentAnalysisVO getStudentAnalysis(Long userId, String periodType) {
        // 1. 顶部概览
        StudentAnalysisVO.OverviewVO overview = getOverview(userId);

        // 2. 每门课程学习统计
        List<StudentAnalysisVO.CourseStatVO> courseStats = getCourseStats(userId);

        // 3. 周期报告
        StudentAnalysisVO.PeriodReportVO periodReport = getPeriodReport(userId, periodType);

        return StudentAnalysisVO.builder()
                .overview(overview)
                .courseStats(courseStats)
                .periodReport(periodReport)
                .build();
    }

    // ========== 1. 顶部概览 ==========
    private StudentAnalysisVO.OverviewVO getOverview(Long userId) {
        // 进行中课程
        List<Long> courseIds = courseStudentMapper.selectList(
                new LambdaQueryWrapper<CourseStudent>().eq(CourseStudent::getUserId, userId)
        ).stream().map(CourseStudent::getCourseId).collect(Collectors.toList());

        int activeCount = 0;
        int completedCount = 0;
        if (!courseIds.isEmpty()) {
            List<Course> courses = courseMapper.selectBatchIds(courseIds);
            activeCount = (int) courses.stream().filter(c -> "active".equals(c.getStatus())).count();
            completedCount = (int) courses.stream().filter(c -> "archived".equals(c.getStatus())).count();
        }

        // 累计积分
        Integer totalPoints = pointsRecordMapper.getLatestPoints(userId);
        if (totalPoints == null) totalPoints = 0;

        return StudentAnalysisVO.OverviewVO.builder()
                .activeCourseCount(activeCount)
                .completedCourseCount(completedCount)
                .totalPoints(totalPoints)
                .build();
    }

    // ========== 2. 每门课程学习统计 ==========
    private List<StudentAnalysisVO.CourseStatVO> getCourseStats(Long userId) {
        // 获取学生所有课程
        List<Long> courseIds = courseStudentMapper.selectList(
                new LambdaQueryWrapper<CourseStudent>().eq(CourseStudent::getUserId, userId)
        ).stream().map(CourseStudent::getCourseId).collect(Collectors.toList());

        if (courseIds.isEmpty()) return new ArrayList<>();

        List<Course> courses = courseMapper.selectBatchIds(courseIds);
        List<StudentAnalysisVO.CourseStatVO> result = new ArrayList<>();

        for (Course course : courses) {
            // 总学习时长：该课程下所有已结束课堂，学生签到时间到课堂结束时间的时长之和
            Long totalDuration = calculateCourseStudyDuration(userId, course.getId());

            // 作业完成次数：该课程下学生已提交的作业数量
            Integer assignmentCount = countCompletedAssignments(userId, course.getId());

            // 作业正确率：该课程下所有已批改作业的总得分/总满分
            Double correctRate = calculateAssignmentCorrectRate(userId, course.getId());

            result.add(StudentAnalysisVO.CourseStatVO.builder()
                    .courseId(course.getId())
                    .courseName(course.getCourseTitle())
                    .totalStudyDuration(totalDuration)
                    .assignmentCompletedCount(assignmentCount)
                    .assignmentCorrectRate(correctRate)
                    .build());
        }

        return result;
    }

    // ========== 2.1 课程学习时长 ==========
    private Long calculateCourseStudyDuration(Long userId, Long courseId) {
        // 查询该课程下所有已结束的课堂
        List<Class> classes = classMapper.selectList(
                new LambdaQueryWrapper<Class>()
                        .eq(Class::getCourseId, courseId)
                        .eq(Class::getStatus, "ended")
        );

        if (classes.isEmpty()) return 0L;

        List<Long> classIds = classes.stream().map(Class::getId).collect(Collectors.toList());

        // 查询该学生的签到记录
        List<ClassCheck> checkins = classCheckMapper.selectList(
                new LambdaQueryWrapper<ClassCheck>()
                        .in(ClassCheck::getClassId, classIds)
                        .eq(ClassCheck::getUserId, userId)
        );

        // 计算签到时间到课堂结束时间的时长
        long totalSeconds = 0;
        for (ClassCheck checkin : checkins) {
            Class clazz = classMapper.selectById(checkin.getClassId());
            if (clazz != null && clazz.getEndTime() != null) {
                long seconds = ChronoUnit.SECONDS.between(checkin.getCheckinTime(), clazz.getEndTime());
                if (seconds > 0) totalSeconds += seconds;
            }
        }

        return totalSeconds;
    }

    // ========== 2.2 作业完成次数 ==========
    private Integer countCompletedAssignments(Long userId, Long courseId) {
        // 查询该课程下所有作业
        List<Assignment> assignments = assignmentMapper.selectList(
                new LambdaQueryWrapper<Assignment>().eq(Assignment::getCourseId, courseId)
        );

        if (assignments.isEmpty()) return 0;

        List<Long> assignmentIds = assignments.stream().map(Assignment::getId).collect(Collectors.toList());

        // 查询该学生已提交的作业（去重）
        List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectList(
                new LambdaQueryWrapper<ObjectSubmit>()
                        .in(ObjectSubmit::getAssignmentId, assignmentIds)
                        .eq(ObjectSubmit::getUserId, userId)
        );
        List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectList(
                new LambdaQueryWrapper<SubjectSubmit>()
                        .in(SubjectSubmit::getAssignmentId, assignmentIds)
                        .eq(SubjectSubmit::getUserId, userId)
        );

        Set<Long> submittedAssignmentIds = new HashSet<>();
        objectSubmits.forEach(o -> submittedAssignmentIds.add(o.getAssignmentId()));
        subjectSubmits.forEach(s -> submittedAssignmentIds.add(s.getAssignmentId()));

        return submittedAssignmentIds.size();
    }

    // ========== 2.3 作业正确率 ==========
    private Double calculateAssignmentCorrectRate(Long userId, Long courseId) {
        // 查询该课程下所有作业
        List<Assignment> assignments = assignmentMapper.selectList(
                new LambdaQueryWrapper<Assignment>().eq(Assignment::getCourseId, courseId)
        );

        if (assignments.isEmpty()) return null;

        List<Long> assignmentIds = assignments.stream().map(Assignment::getId).collect(Collectors.toList());

        // 客观题提交
        List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectList(
                new LambdaQueryWrapper<ObjectSubmit>()
                        .in(ObjectSubmit::getAssignmentId, assignmentIds)
                        .eq(ObjectSubmit::getUserId, userId)
        );

        // 主观题提交（只取已批改的）
        List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectList(
                new LambdaQueryWrapper<SubjectSubmit>()
                        .in(SubjectSubmit::getAssignmentId, assignmentIds)
                        .eq(SubjectSubmit::getUserId, userId)
                        .eq(SubjectSubmit::getGradingStatus, 2)
        );

        // 如果没有任何已批改的作业，返回 null
        if (objectSubmits.isEmpty() && subjectSubmits.isEmpty()) return null;

        // 计算已批改作业的总得分
        int totalScore = 0;
        int totalMaxScore = 0;

        // 客观题：按 assignment 分组计算
        Map<Long, List<ObjectSubmit>> objByAssignment = objectSubmits.stream()
                .collect(Collectors.groupingBy(ObjectSubmit::getAssignmentId));

        for (Map.Entry<Long, List<ObjectSubmit>> entry : objByAssignment.entrySet()) {
            Assignment assignment = assignmentMapper.selectById(entry.getKey());
            if (assignment != null) {
                int objScore = entry.getValue().stream()
                        .mapToInt(o -> o.getObjectScore() != null ? o.getObjectScore() : 0)
                        .sum();
                totalScore += objScore;
                // 客观题满分：该作业下所有客观题 perscore 之和，这里直接用 assignment.maxScore
                totalMaxScore += assignment.getMaxScore();
            }
        }

        // 主观题
        for (SubjectSubmit ss : subjectSubmits) {
            Assignment assignment = assignmentMapper.selectById(ss.getAssignmentId());
            if (assignment != null) {
                totalScore += ss.getSubjectScore() != null ? ss.getSubjectScore() : 0;
                totalMaxScore += assignment.getMaxScore();
            }
        }

        if (totalMaxScore == 0) return null;

        return Math.round((double) totalScore / totalMaxScore * 10000) / 100.0;
    }

    // ========== 3. 周期报告 ==========
    private StudentAnalysisVO.PeriodReportVO getPeriodReport(Long userId, String periodType) {
        boolean isMonth = "month".equalsIgnoreCase(periodType);

        // 当前周期时间范围
        LocalDateTime currentStart = isMonth ?
                LocalDate.now().withDayOfMonth(1).atStartOfDay() :
                LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime currentEnd = LocalDateTime.now();

        // 上一周期时间范围
        LocalDateTime previousStart = isMonth ?
                LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay() :
                LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime previousEnd = isMonth ?
                LocalDate.now().withDayOfMonth(1).atStartOfDay() :
                LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();

        // 当前周期数据
        StudentAnalysisVO.PeriodReportVO current = getPeriodData(userId, currentStart, currentEnd);

        // 上一周期数据
        StudentAnalysisVO.PeriodReportVO previous = getPeriodData(userId, previousStart, previousEnd);

        // 计算趋势
        StudentAnalysisVO.PeriodReportVO.TrendVO trend = StudentAnalysisVO.PeriodReportVO.TrendVO.builder()
                .durationChange(calculateChange(current.getStudyDuration(), previous.getStudyDuration()))
                .correctRateChange(calculateChange(current.getAssignmentCorrectRate(), previous.getAssignmentCorrectRate()))
                .build();

        return StudentAnalysisVO.PeriodReportVO.builder()
                .studyDuration(current.getStudyDuration())
                .assignmentCompletedCount(current.getAssignmentCompletedCount())
                .assignmentCorrectRate(current.getAssignmentCorrectRate())
                .trend(trend)
                .build();
    }

    private StudentAnalysisVO.PeriodReportVO getPeriodData(Long userId, LocalDateTime start, LocalDateTime end) {
        // 1. 课堂学习时长
        Long classDuration = calculateClassDurationInPeriod(userId, start, end);

        // 2. 自习室学习时长（复用同事接口）
        Long studyRoomDuration = 0L;
        try {
            // 注意：同事的接口返回的是本周/本月总时长，需要按周期过滤
            // 这里简单复用，实际需要根据 start/end 过滤 study_room 表
            StudyRoomStatisticVO stats = studyRoomService.getStudyStatistic(userId);
            if (start.getDayOfMonth() == 1) {
                studyRoomDuration = stats.getMonthStudyDuration() != null ? stats.getMonthStudyDuration().longValue() : 0L;
            } else {
                studyRoomDuration = stats.getWeekStudyDuration() != null ? stats.getWeekStudyDuration().longValue() : 0L;
            }
        } catch (Exception e) {
            log.warn("获取自习室时长失败: {}", e.getMessage());
        }

        Long totalDuration = classDuration + studyRoomDuration;

        // 3. 作业完成次数
        Integer assignmentCount = countAssignmentsInPeriod(userId, start, end);

        // 4. 作业正确率
        Double correctRate = calculateCorrectRateInPeriod(userId, start, end);

        return StudentAnalysisVO.PeriodReportVO.builder()
                .studyDuration(totalDuration)
                .assignmentCompletedCount(assignmentCount)
                .assignmentCorrectRate(correctRate)
                .build();
    }

    private Long calculateClassDurationInPeriod(Long userId, LocalDateTime start, LocalDateTime end) {
        // 查询该学生在时间范围内的签到记录
        List<ClassCheck> checkins = classCheckMapper.selectList(
                new LambdaQueryWrapper<ClassCheck>()
                        .eq(ClassCheck::getUserId, userId)
                        .ge(ClassCheck::getCheckinTime, start)
                        .le(ClassCheck::getCheckinTime, end)
        );

        if (checkins.isEmpty()) return 0L;

        long totalSeconds = 0;
        for (ClassCheck checkin : checkins) {
            Class clazz = classMapper.selectById(checkin.getClassId());
            if (clazz != null && clazz.getEndTime() != null) {
                long seconds = ChronoUnit.SECONDS.between(checkin.getCheckinTime(), clazz.getEndTime());
                if (seconds > 0) totalSeconds += seconds;
            }
        }

        return totalSeconds;
    }

    private Integer countAssignmentsInPeriod(Long userId, LocalDateTime start, LocalDateTime end) {
        // 查询时间范围内提交的作业
        List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectList(
                new LambdaQueryWrapper<ObjectSubmit>()
                        .eq(ObjectSubmit::getUserId, userId)
                        .ge(ObjectSubmit::getSubmitTime, start)
                        .le(ObjectSubmit::getSubmitTime, end)
        );
        List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectList(
                new LambdaQueryWrapper<SubjectSubmit>()
                        .eq(SubjectSubmit::getUserId, userId)
                        .ge(SubjectSubmit::getFinishTime, start)
                        .le(SubjectSubmit::getFinishTime, end)
        );

        Set<Long> assignmentIds = new HashSet<>();
        objectSubmits.forEach(o -> assignmentIds.add(o.getAssignmentId()));
        subjectSubmits.forEach(s -> assignmentIds.add(s.getAssignmentId()));

        return assignmentIds.size();
    }

    private Double calculateCorrectRateInPeriod(Long userId, LocalDateTime start, LocalDateTime end) {
        // 查询时间范围内已批改的作业
        List<SubjectSubmit> subjectSubmits = subjectSubmitMapper.selectList(
                new LambdaQueryWrapper<SubjectSubmit>()
                        .eq(SubjectSubmit::getUserId, userId)
                        .eq(SubjectSubmit::getGradingStatus, 2)
                        .ge(SubjectSubmit::getGradingTime, start)
                        .le(SubjectSubmit::getGradingTime, end)
        );

        List<ObjectSubmit> objectSubmits = objectSubmitMapper.selectList(
                new LambdaQueryWrapper<ObjectSubmit>()
                        .eq(ObjectSubmit::getUserId, userId)
                        .ge(ObjectSubmit::getSubmitTime, start)
                        .le(ObjectSubmit::getSubmitTime, end)
        );

        if (objectSubmits.isEmpty() && subjectSubmits.isEmpty()) return null;

        int totalScore = 0;
        int totalMaxScore = 0;

        // 客观题
        for (ObjectSubmit os : objectSubmits) {
            Assignment assignment = assignmentMapper.selectById(os.getAssignmentId());
            if (assignment != null) {
                totalScore += os.getObjectScore() != null ? os.getObjectScore() : 0;
                totalMaxScore += assignment.getMaxScore();
            }
        }

        // 主观题
        for (SubjectSubmit ss : subjectSubmits) {
            Assignment assignment = assignmentMapper.selectById(ss.getAssignmentId());
            if (assignment != null) {
                totalScore += ss.getSubjectScore() != null ? ss.getSubjectScore() : 0;
                totalMaxScore += assignment.getMaxScore();
            }
        }

        if (totalMaxScore == 0) return null;

        return Math.round((double) totalScore / totalMaxScore * 10000) / 100.0;
    }

    private Double calculateChange(Double current, Double previous) {
        if (previous == null || previous == 0) return 0.0;
        return Math.round(((current != null ? current : 0) - previous) / previous * 10000) / 100.0;
    }

    private Double calculateChange(Long current, Long previous) {
        if (previous == null || previous == 0) return 0.0;
        long cur = current != null ? current : 0;
        return Math.round((double) (cur - previous) / previous * 10000) / 100.0;
    }
}