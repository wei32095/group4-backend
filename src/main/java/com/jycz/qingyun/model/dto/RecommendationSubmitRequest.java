package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecommendationSubmitRequest {

    @NotNull(message = "推荐记录ID不能为空")
    private Long recommendationId;

    @NotBlank(message = "答案不能为空")
    private String answer;
}