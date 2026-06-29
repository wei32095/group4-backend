package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CourseCreateVO {

    private Long id;
    private Long userId;
    private String courseTitle;
    private String description;
    private String cover;
    private Integer studentCount;
    private String courseCode;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}