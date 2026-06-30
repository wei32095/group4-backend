package com.jycz.qingyun.service;

public interface VerifyCodeService {

    /**
     * 发送验证码到指定手机号
     * @param phone 手机号
     * @return 验证码（mock 模式直接返回，上线后去掉）
     */
    String sendCode(String phone);

    /**
     * 校验验证码
     * @param phone 手机号
     * @param code  验证码
     * @return true-校验通过，false-校验失败
     */
    boolean verifyCode(String phone, String code);
}
