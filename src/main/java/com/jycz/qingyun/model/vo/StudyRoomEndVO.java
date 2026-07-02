package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyRoomEndVO {
    private Long id;
    private LocalDateTime endTime;
    private Integer totalTime;
    private Integer focusMode;
    private Integer earnedPoints;      // 本次自习获得的积分
}
