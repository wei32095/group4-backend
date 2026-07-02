package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("flower")
public class Flower {
    private Long id;
    private Long userId;
    private Long seedId;
    private Integer sunlight;
    private Integer water;
    private Integer nutrient;
    private Integer growthValue;
    private Integer stage;
    private Integer isUnlocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
