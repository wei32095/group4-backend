package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentStudentListVO {

    private Long assignmentId;
    private String assignmentTitle;
    private LocalDateTime deadline;
    private Integer maxScore;
    private String status;
    private Integer myScore;
    private LocalDateTime createdAt;
}