package com.jycz.qingyun.model.dto;

import lombok.Data;

/**
 * 编辑种子请求体 — 所有字段可选，只传需要修改的字段。
 */
@Data
public class UpdateSeedRequest {

    private String variety;

    private String description;

    private Integer maxGrowth;

    private String image;

    private String stage0Image;

    private String stage1Image;

    private String stage2Image;

    private String stage3Image;

    private Integer price;
}
