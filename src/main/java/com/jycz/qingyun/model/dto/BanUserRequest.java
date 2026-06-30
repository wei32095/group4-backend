package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BanUserRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "封禁状态不能为空")
    private Integer status;  // 0-封禁，1-解封

    private LocalDateTime banExpireTime;

    private String banReason;
}