package com.jycz.qingyun.model.vo;

import lombok.Data;

@Data
public class SeedVO {
    private Long id;
    private String variety;
    private String description;
    private Integer maxGrowth;
    private String image;
    private Integer price;
}
