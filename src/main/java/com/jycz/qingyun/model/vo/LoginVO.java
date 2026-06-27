package com.jycz.qingyun.model.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String token;      // JWT Token
    private String tokenType = "Bearer"; // 固定为 Bearer
    private String userType;   // STUDENT / TEACHER / ADMIN
    private Object userInfo;   // 用户信息（StudentVO / TeacherVO / AdminVO）
}