package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动记录 - 响应体
 */
@Data
public class ActivityVO {
    private String content;
    private LocalDateTime time;
}
