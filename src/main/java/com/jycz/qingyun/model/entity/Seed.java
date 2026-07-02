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
    private String image;
    private String stage0Image;
    private String stage1Image;
    private String stage2Image;
    private String stage3Image;
    private Integer price;
    private Integer sunlightMax;
    private Integer waterMax;
    private Integer nutrientMax;
    private Integer isDeleted;
    private LocalDateTime createdAt;
}
