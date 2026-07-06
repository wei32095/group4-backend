package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.BanUserRequest;
import com.jycz.qingyun.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/qingyun/user")
@RequiredArgsConstructor
public class BanUserController {

    private final UserService userService;

    /**
     * 管理员封禁用户
     * POST /qingyun/user/ban
     */
    @PostMapping("/ban")
    public ApiResult<Void> banUser(
            @Valid @RequestBody BanUserRequest request,
            HttpServletRequest httpRequest) {

        Long adminId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        userService.banUser(request, adminId);
        return ApiResult.success();
    }

    /**
     * 管理员解封用户
     * POST /qingyun/user/unban
     */
    @PostMapping("/unban")
    public ApiResult<Void> unbanUser(
            @Valid @RequestBody BanUserRequest request,
            HttpServletRequest httpRequest) {

        Long adminId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        userService.unbanUser(request.getUserId(), adminId);
        return ApiResult.success();
    }
}