package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class RecommendationSubmitVO {

    private Long recommendationId;
    private String status;
    private LocalDateTime submitTime;
    private Integer score;
    private Integer maxScore;
    private Integer correctCount;
    private Integer totalCount;
    private Boolean allCorrect;
    private Integer pointsEarned;
    private List<Map<String, Object>> newRecommendations;
}