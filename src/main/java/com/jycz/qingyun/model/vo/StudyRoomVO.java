package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyRoomVO {
    private Long id;
    private Integer mode;
    private LocalDateTime startTime;
    private Integer planTime;
}
