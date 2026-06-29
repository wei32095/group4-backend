package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourseListStudentVO {

    private Long courseId;
    private String courseTitle;//
    private String cover;
    private String teacherName;
    private Integer studentCount;
    private LocalDateTime joinedAt;
}