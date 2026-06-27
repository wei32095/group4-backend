package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.LoginRequest;
import com.jycz.qingyun.model.vo.Login;

public interface UserService {

    /**
     * 登录
     */
    ApiResult<Login> login(LoginRequest request);
}
