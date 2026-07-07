package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RecommendationQuestionVO {

    private Long recommendationId;
    private Long weakPointId;
    private String knowledgePoint;
    private Map<String, Object> question;
}