package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PointsRecordVO {
    private Long id;
    private Long userId;
    private Integer changeType;
    private Integer changePoints;
    private Integer leftPoints;
    private Integer sourceType;
    private LocalDateTime changeTime;
}
