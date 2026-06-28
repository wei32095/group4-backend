package com.jycz.qingyun.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WxUtil {

    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Value("${wx.mock}")
    private boolean mock;

    /**
     * 用临时 code 换取 openid
     * mock 模式下直接返回基于 code 构造的 fake openid
     */
    public String codeToOpenid(String code) {
        if (mock) {
            return "mock_openid_" + code;
        }
        // 真实逻辑：调微信 https://api.weixin.qq.com/sns/jscode2session
        // 后续接入真实 AppID/AppSecret 后取消 mock
        throw new UnsupportedOperationException("请配置真实的微信 AppID 和 AppSecret");
    }

    /**
     * 解密手机号
     * mock 模式下返回一个假手机号
     */
    public String decryptPhone(String encryptedData, String iv, String openid) {
        if (mock) {
            return "13800001111";
        }
        // 真实逻辑：AES 解密 encryptedData
        // 后续接入真实 AppID/AppSecret 后取消 mock
        throw new UnsupportedOperationException("请配置真实的微信 AppID 和 AppSecret");
    }
}
