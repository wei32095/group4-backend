package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyRoomRecordVO {
    private Long id;
    private Integer mode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalTime;
    private Integer screenSwitchCount;
    private Integer planTime;
    private Integer isValid;
}
