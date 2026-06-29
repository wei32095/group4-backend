package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data//
public class ClassChatSendRequest {

    @NotNull(message = "课堂ID不能为空")
    private Long classId;

    @NotNull(message = "消息类型不能为空")
    private Integer messageType;  // 1-文字，2-图片

    @NotBlank(message = "消息内容不能为空")
    private String content;
}