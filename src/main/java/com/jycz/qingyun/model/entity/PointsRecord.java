package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("points_record")
public class PointsRecord {
    private Long id;
    private Long userId;
    private Integer changeType;      // 1-获取，2-消耗
    private Integer changePoints;    // 变动值（正数）
    private Integer leftPoints;      // 变动后余额
    private Integer sourceType;      // 1-签到 2-投票 3-作业 4-自习 5-道具
    private LocalDateTime changeTime;
}
