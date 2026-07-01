package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("seed")
public class Seed {
    private Long id;
    private String variety;
    private String description;
    private Integer maxGrowth;
    private String image;
    private String stage0Image;
    private String stage1Image;
    private String stage2Image;
    private String stage3Image;
    private String stage4Image;
    private Integer price;
    private LocalDateTime createdAt;
}
