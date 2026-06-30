package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProblemReplyRequest {

    @NotNull(message = "问题ID不能为空")
    private Long problemId;

    @NotBlank(message = "回复内容不能为空")
    private String content;
}