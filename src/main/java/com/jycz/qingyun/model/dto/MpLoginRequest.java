package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MpLoginRequest {

    @NotBlank(message = "code 不能为空")
    private String code;
}
