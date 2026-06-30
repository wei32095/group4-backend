package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserCourseVO {

    private Long courseId;
    private String courseTitle;
    private String teacherName;
    private Integer studentCount;
    private String courseCode;
    private String status;
    private LocalDateTime joinedAt;
    private String relationType;  // "joined" 或 "created"
}