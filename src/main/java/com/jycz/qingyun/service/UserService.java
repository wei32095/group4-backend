package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.InfoUpdateRequest;
import com.jycz.qingyun.model.dto.LoginRequest;
import com.jycz.qingyun.model.vo.LoginVO;
import com.jycz.qingyun.model.vo.StudentInfoVO;

public interface UserService {

    /**
     * 登录
     */
    ApiResult<LoginVO> login(LoginRequest request);

    /**
     * 获取当前登录用户信息
     */
    ApiResult<StudentInfoVO> getUserInfo(Long userId);

    /**
     * 修改个人信息
     */
    ApiResult<Boolean> updateUserInfo(Long userId, InfoUpdateRequest request);
}
