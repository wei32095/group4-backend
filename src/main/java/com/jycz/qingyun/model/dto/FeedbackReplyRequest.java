package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackReplyRequest {

    @NotBlank(message = "回复内容不能为空")
    private String replyContent;
}
