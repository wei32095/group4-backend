package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlowerVO {
    private Long id;
    private Long seedId;
    private String variety;
    private Integer sunlight;
    private Integer water;
    private Integer nutrient;
    private Integer growthPercent;
    private Integer stage;
    private Integer isUnlocked;
    private LocalDateTime createdAt;
}
