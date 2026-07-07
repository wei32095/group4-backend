package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class RecommendationListVO {

    private Long recommendationId;
    private Long assignmentId;
    private String assignmentTitle;
    private Long courseId;
    private String courseName;
    private String status;
    private Boolean isCompleted;
    private List<Map<String, Object>> questions;
}