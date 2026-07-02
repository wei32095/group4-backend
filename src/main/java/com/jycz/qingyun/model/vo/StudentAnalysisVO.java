package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentAnalysisVO {

    private OverviewVO overview;
    private List<CourseStatVO> courseStats;
    private PeriodReportVO periodReport;

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
        private Long totalStudyDuration;      // 秒
        private Integer assignmentCompletedCount;
        private Double assignmentCorrectRate; // 百分比
    }

    @Data
    @Builder
    public static class PeriodReportVO {
        private Long studyDuration;           // 秒（课堂 + 自习室）
        private Integer assignmentCompletedCount;
        private Double assignmentCorrectRate; // 百分比
        private TrendVO trend;

        @Data
        @Builder
        public static class TrendVO {
            private Double durationChange;     // 学习时长变化百分比
            private Double correctRateChange;  // 正确率变化百分比
        }
    }
}