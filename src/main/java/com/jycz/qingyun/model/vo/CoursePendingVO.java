package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CoursePendingVO {

    private Long courseId;
    private String courseTitle;
    private Long teacherId;
    private String teacherName;
    private String description;
    private String cover;
    private String courseCode;
    private String status;
    private Integer auditStatus;
    private LocalDateTime createdAt;
}