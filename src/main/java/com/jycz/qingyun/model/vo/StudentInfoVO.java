package com.jycz.qingyun.model.vo;

import lombok.Data;

/**
 * 用户个人信息（仅返回可对外暴露的字段，不含密码）
 */
@Data
public class StudentInfoVO {
    private Long id;
    private String name;
    private String phone;
    private String avatar;
    private Integer role;
    private Integer status;
}
