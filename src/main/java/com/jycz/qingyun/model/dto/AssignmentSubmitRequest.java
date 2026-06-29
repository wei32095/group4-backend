package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssignmentSubmitRequest {
//
    @NotNull(message = "作业ID不能为空")
    private Long assignmentId;

    @NotNull(message = "答案列表不能为空")
    private List<AnswerRequest> answers;

    @Data
    public static class AnswerRequest {
        @NotNull(message = "题目ID不能为空")
        private Long questionId;

        private String answer;
    }
}