package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("shop_item")
public class ShopItem {
    private Long id;
    private String itemName;
    private String icon;
    private Integer price;
    private Integer growthValue;
    private LocalDateTime createdAt;
}
