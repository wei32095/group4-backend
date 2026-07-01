package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VoteActiveListVO {

    private Long voteId;
    private String heading;
    private List<String> options;
    private String status;
    private LocalDateTime endedAt;
    private Boolean hasVoted;
}