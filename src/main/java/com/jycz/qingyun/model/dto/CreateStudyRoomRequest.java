package com.jycz.qingyun.model.dto;

import lombok.Data;

@Data
public class CreateStudyRoomRequest {
    private Integer mode;     // 1-正向计时，2-倒计时
    private Integer planTime; // 计划时长（秒），mode=2 时必填
}
