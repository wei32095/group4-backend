package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.BindPhoneRequest;
import com.jycz.qingyun.model.dto.InfoUpdateRequest;
import com.jycz.qingyun.model.dto.PasswordUpdateRequest;
import com.jycz.qingyun.model.vo.StudentInfoVO;
import com.jycz.qingyun.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qingyun")
public class InfoController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public ApiResult<StudentInfoVO> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.getUserInfo(userId);
    }

    @PutMapping("/info")
    public ApiResult<Boolean> updateInfo(HttpServletRequest request,
                                          @Valid @RequestBody InfoUpdateRequest body) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.updateUserInfo(userId, body);
    }

    @PostMapping("/bind-phone")
    public ApiResult<Boolean> bindPhone(HttpServletRequest request,
                                         @Valid @RequestBody BindPhoneRequest body) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.bindPhone(userId, body);
    }

    @PutMapping("/password")
    public ApiResult<Boolean> updatePassword(HttpServletRequest request,
                                              @Valid @RequestBody PasswordUpdateRequest body) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.updatePassword(userId, body);
    }

}
