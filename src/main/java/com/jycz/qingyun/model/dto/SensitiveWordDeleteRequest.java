package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SensitiveWordDeleteRequest {

    @NotNull(message = "敏感词ID不能为空")
    private Long id;
}