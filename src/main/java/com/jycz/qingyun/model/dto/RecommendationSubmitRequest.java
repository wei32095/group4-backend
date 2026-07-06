package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RecommendationSubmitRequest {

    @NotNull(message = "推荐记录ID不能为空")
    private Long recommendationId;

    @NotNull(message = "答案列表不能为空")
    private List<AnswerRequest> answers;

    @Data
    public static class AnswerRequest {
        @NotNull(message = "题目序号不能为空")
        private Integer sortOrder;

        private String answer;
    }
}