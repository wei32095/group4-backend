package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentTeacherListVO {

    private Long assignmentId;
    private String assignmentTitle;
    private LocalDateTime deadline;
    private Integer maxScore;
    private Integer submitCount;
    private Integer totalStudents;
    private Double submissionRate;
    private Double avgScore;
    private LocalDateTime createdAt;
}