package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentCreateVO {
//
    private Long assignmentId;
    private Long courseId;
    private String assignmentTitle;
    private LocalDateTime deadline;
    private Integer maxScore;
    private Integer questionCount;
    private LocalDateTime createdAt;
}