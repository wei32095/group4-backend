package com.jycz.qingyun.model.vo;

import lombok.Data;

@Data
public class ShopItemVO {
    private Long id;
    private String itemName;
    private String icon;
    private Integer price;
    private Integer growthValue;
}
