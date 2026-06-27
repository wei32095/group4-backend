package com.jycz.qingyun.model.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;    // 手机号
    private String password; // 密码
}