package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VoteCreateVO {

    private Long id;
    private Long classId;
    private String heading;
    private List<String> options;
    private String correctOption;
    private Integer duration;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
}