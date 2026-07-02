package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourseResourceVO {

    private Long resourceId;
    private Long courseId;
    private Long userId;
    private String teacherName;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String description;
    private Integer downloadCount;
    private LocalDateTime createdAt;
}