package com.jycz.qingyun.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseResponse {
    private Long courseId;
    private String name;
    private String description;
    private String coverImage;
    private Long teacherId;
    private String teacherName;
    private String courseCode;
    private String status;
    private LocalDateTime createdAt;
}