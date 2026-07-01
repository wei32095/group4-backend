package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈详情 - 含回复内容
 */
@Data
public class FeedbackDetailVO {
    private Long id;
    private Long userId;
    private String userName;
    private Integer userRole;
    private String content;
    private Integer status;
    private String replyContent;
    private LocalDateTime replyTime;
    private LocalDateTime createdAt;
}
