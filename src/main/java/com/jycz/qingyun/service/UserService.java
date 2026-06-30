package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.*;
import com.jycz.qingyun.model.vo.LoginVO;
import com.jycz.qingyun.model.vo.StudentInfoVO;

public interface UserService {

    /**
     * 登录
     */
    ApiResult<LoginVO> login(LoginRequest request);

    /**
     * 验证码登录
     */
    ApiResult<LoginVO> loginByVerifyCode(VerifyCodeLoginRequest request);

    /**
     * 注册
     */
    ApiResult<LoginVO> register(RegisterRequest request);

    /**
     * 小程序登录（code → openid → 自动注册/登录）
     */
    ApiResult<LoginVO> loginByMpCode(MpLoginRequest request);

    /**
     * 绑定手机号（小程序端，需登录）
     */
    ApiResult<Boolean> bindPhone(Long userId, BindPhoneRequest request);

    /**
     * 获取当前登录用户信息
     */
    ApiResult<StudentInfoVO> getUserInfo(Long userId);

    /**
     * 修改个人信息
     */
    ApiResult<Boolean> updateUserInfo(Long userId, InfoUpdateRequest request);

    /**
     * 修改密码
     */
    ApiResult<Boolean> updatePassword(Long userId, PasswordUpdateRequest request);
    void banUser(BanUserRequest request, Long adminId);
}
