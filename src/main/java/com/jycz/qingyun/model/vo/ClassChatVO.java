package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClassChatVO {

    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Integer messageType;
    private String content;
    private LocalDateTime sentTime;
}