package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_room")
public class StudyRoom {
    private Long id;
    private Long userId;
    private String goal;           // 自习目标
    private Integer mode;          // 1-正向计时，2-倒计时
    private Integer focusMode;     // 0-普通，1-番茄钟
    private Integer planTime;      // 计划时长（秒），倒计时用
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalTime;     // 总时长（秒）
    private LocalDateTime createdAt;
}
