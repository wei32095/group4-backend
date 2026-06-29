package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentGradeVO {
//
    private Long assignmentId;
    private Long studentId;
    private String studentName;
    private Integer totalScore;
    private Integer maxScore;
    private LocalDateTime gradedAt;
}