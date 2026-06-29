package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourseListTeacherVO {

    private Long courseId;//
    private String courseTitle;
    private String cover;
    private Integer studentCount;
    private String courseCode;
    private String status;
    private LocalDateTime createdAt;
}