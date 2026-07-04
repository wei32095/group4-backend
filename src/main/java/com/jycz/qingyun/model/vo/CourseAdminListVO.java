package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourseAdminListVO {

    private Long courseId;
    private String courseTitle;
    private String cover;              // ← 新增
    private Long teacherId;
    private String teacherName;
    private Integer studentCount;
    private String courseCode;
    private String status;
    private Integer auditStatus;
    private String auditRemark;
    private LocalDateTime auditTime;
    private LocalDateTime createdAt;
}