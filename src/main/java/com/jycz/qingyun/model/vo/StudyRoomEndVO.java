package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyRoomEndVO {
    private Long id;
    private LocalDateTime endTime;
    private Integer totalTime;
    private Integer isValid;
}
