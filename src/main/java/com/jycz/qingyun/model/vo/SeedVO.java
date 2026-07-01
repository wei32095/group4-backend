package com.jycz.qingyun.model.vo;

import lombok.Data;

@Data
public class SeedVO {
    private Long id;
    private String variety;
    private String description;
    private Integer sunlightMax;
    private Integer waterMax;
    private Integer nutrientMax;
    private String image;
    private String stage0Image;
    private String stage1Image;
    private String stage2Image;
    private String stage3Image;
    private Integer price;
    private Integer isDeleted;
}
