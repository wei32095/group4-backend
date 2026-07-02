package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyRoomRecordVO {
    private Long id;
    private String goal;
    private Integer mode;
    private Integer focusMode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalTime;
    private Integer planTime;
}
