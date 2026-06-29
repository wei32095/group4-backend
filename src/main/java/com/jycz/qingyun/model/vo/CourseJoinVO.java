package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CourseJoinVO {

    private Long courseId;
    private String courseTitle;//
    private String description;
    private String cover;
    private Integer studentCount;
    private String teacherName;
    private LocalDateTime joinedAt;
}