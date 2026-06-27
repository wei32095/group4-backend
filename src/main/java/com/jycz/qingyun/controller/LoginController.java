package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.LoginRequest;
import com.jycz.qingyun.service.UserService;
import com.jycz.qingyun.model.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qingyun")
public class LoginController {

    @Autowired
    private UserService userService;  // 注入的是接口，不是实现类

    @PostMapping("/login")
    public ApiResult<LoginVO> login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
}