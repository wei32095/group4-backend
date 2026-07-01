package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CourseDetailVO {

    private Long id;
    private String courseTitle;
    private String description;
    private String cover;
    private Integer studentCount;
    private String courseCode;
    private String status;

    private String teacherName;
    private String teacherAvatar;
    private LocalDateTime createdAt;
}