package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyRoomVO {
    private Long id;
    private String goal;
    private Integer mode;
    private Integer focusMode;
    private LocalDateTime startTime;
    private Integer planTime;
}
