package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackSubmitRequest {

    @NotBlank(message = "反馈内容不能为空")
    private String content;
}
