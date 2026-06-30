package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.LoginRequest;
import com.jycz.qingyun.model.dto.RegisterRequest;
import com.jycz.qingyun.model.dto.VerifyCodeLoginRequest;
import com.jycz.qingyun.service.UserService;
import com.jycz.qingyun.model.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qingyun")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResult<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/login/verify-code")
    public ApiResult<LoginVO> loginByVerifyCode(@Valid @RequestBody VerifyCodeLoginRequest request) {
        return userService.loginByVerifyCode(request);
    }

    @PostMapping("/register")
    public ApiResult<LoginVO> register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }
}