package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentAnalysisVO {

    private OverviewVO overview;
    private List<CourseStatVO> courseStats;
    private PeriodReportVO weekReport;    // ← 本周报告
    private PeriodReportVO monthReport;   // ← 本月报告

    @Data
    @Builder
    public static class OverviewVO {
        private Integer activeCourseCount;
        private Integer completedCourseCount;
        private Integer totalPoints;
    }

    @Data
    @Builder
    public static class CourseStatVO {
        private Long courseId;
        private String courseName;
        private Long totalStudyDuration;
        private Integer assignmentCompletedCount;
        private Double assignmentCorrectRate;
    }

    @Data
    @Builder
    public static class PeriodReportVO {
        private Long studyDuration;
        private Integer assignmentCompletedCount;
        private Double assignmentCorrectRate;
        private TrendVO trend;

        @Data
        @Builder
        public static class TrendVO {
            private Double durationChange;
            private Double correctRateChange;
        }
    }
}