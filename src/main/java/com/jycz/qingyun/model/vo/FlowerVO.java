package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlowerVO {
    private Long id;
    private String variety;
    private Integer stage;
    private Integer currentValue;
    private Integer maxGrowth;
    private Integer isUnlocked;
    private LocalDateTime createdAt;
}
