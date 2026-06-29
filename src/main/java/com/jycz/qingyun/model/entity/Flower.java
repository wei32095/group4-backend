package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("flower")
public class Flower {
    private Long id;
    private Long userId;
    private String variety;
    @TableField("growth_growth_value")
    private Integer growthGrowthValue;
    private Integer stage;
    private Integer isUnlocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
