package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VoteResultVO {

    private Long id;
    private String heading;
    private List<String> options;
    private Integer totalVoters;
    private String correctOption;
    private List<OptionStatVO> statistics;
    private Double correctRate;

    @Data
    @Builder
    public static class OptionStatVO {
        private String option;
        private Integer count;
        private Double percentage;
    }
}