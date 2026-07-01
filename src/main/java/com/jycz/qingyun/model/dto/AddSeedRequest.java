package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddSeedRequest {

    @NotBlank(message = "品种名称不能为空")
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

    @NotNull(message = "价格不能为空")
    private Integer price;
}
