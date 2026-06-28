package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BindPhoneRequest {

    @NotBlank(message = "encryptedData 不能为空")
    private String encryptedData;

    @NotBlank(message = "iv 不能为空")
    private String iv;
}
