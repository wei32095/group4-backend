package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈列表 - 单条视图（不含回复详情）
 */
@Data
public class FeedbackVO {
    private Long id;
    private Long userId;
    private String userName;
    private Integer userRole;
    private String content;
    private Integer status;     // 0-待处理，1-已回复
    private LocalDateTime createdAt;
}
