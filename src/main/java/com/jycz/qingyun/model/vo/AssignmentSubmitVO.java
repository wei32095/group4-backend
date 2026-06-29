package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentSubmitVO {

    private Long assignmentId;
    private String status;
    private LocalDateTime submitTime;
    private Integer autoScore;
    private Integer maxScore;
    private Boolean subjectivePending;
}