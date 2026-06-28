package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "手机号不能为空")
    @Size(min = 11, max = 11, message = "手机号必须为11位")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度6-50位")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名最长不超过50字")
    private String name;

    @Min(value = 1, message = "角色不合法")
    @Max(value = 2, message = "角色不合法")
    private Integer role;
}
