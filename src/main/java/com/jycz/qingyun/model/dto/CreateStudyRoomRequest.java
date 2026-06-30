package com.jycz.qingyun.model.dto;

import lombok.Data;

@Data
public class CreateStudyRoomRequest {
    private String goal;      // 自习目标
    private Integer mode;     // 1-正向计时，2-倒计时
    private Integer planTime; // 计划时长（秒），倒计时用
    private Integer focusMode; // 0-普通，1-番茄钟
}
