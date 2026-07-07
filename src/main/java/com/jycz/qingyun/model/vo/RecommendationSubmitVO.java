package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RecommendationSubmitVO {

    private Long recommendationId;
    private Boolean isCorrect;
    private Integer pointsEarned;
    private String message;
    private Map<String, Object> newQuestion;
}