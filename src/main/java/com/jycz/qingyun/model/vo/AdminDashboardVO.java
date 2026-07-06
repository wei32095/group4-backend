package com.jycz.qingyun.model.vo;

import lombok.Data;

/**
 * 管理员看板 - 响应体
 */
@Data
public class AdminDashboardVO {
    private UserStats userStats;
    private CourseStats courseStats;
    private TodayActivity todayActivity;
    private long pendingFeedbackCount;

    @Data
    public static class UserStats {
        private long totalUsers;
        private long newUsersToday;
        private long newUsersWeek;
        private long newUsersMonth;
    }

    @Data
    public static class CourseStats {
        private long totalCourses;
        private long pendingCourses;
        private long activeCourses;
    }

    @Data
    public static class TodayActivity {
        private long studyUsers;
        private long checkins;
        private long activeUsers;
    }
}
