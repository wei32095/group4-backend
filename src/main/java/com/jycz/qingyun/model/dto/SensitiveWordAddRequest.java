package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SensitiveWordAddRequest {

    @NotBlank(message = "敏感词不能为空")
    @Size(max = 50, message = "敏感词最多50个字符")
    private String word;
}