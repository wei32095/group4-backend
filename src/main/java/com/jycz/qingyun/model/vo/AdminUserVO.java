package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员查看用户列表 - 单用户视图
 * 不含密码/openid 等敏感字段
 */
@Data
public class AdminUserVO {
    private Long id;
    private String name;
    private String phone;
    private String avatar;
    private String bio;
    private Integer role;
    private Integer status;
    private Integer points;
    private LocalDateTime banExpireTime;
    private String banReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
