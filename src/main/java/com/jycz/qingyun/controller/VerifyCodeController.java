package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.CheckVerifyCodeRequest;
import com.jycz.qingyun.model.dto.SendVerifyCodeRequest;
import com.jycz.qingyun.service.VerifyCodeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qingyun")
public class VerifyCodeController {

    @Autowired
    private VerifyCodeService verifyCodeService;

    @PostMapping("/verify-code/send")
    public ApiResult<String> sendCode(@Valid @RequestBody SendVerifyCodeRequest request) {
        try {
            String code = verifyCodeService.sendCode(request.getPhone());
            return ApiResult.success("验证码已发送", code);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }

    @PostMapping("/verify-code/check")
    public ApiResult<Void> checkCode(@Valid @RequestBody CheckVerifyCodeRequest request) {
        boolean ok = verifyCodeService.verifyCode(request.getPhone(), request.getCode());
        if (ok) {
            return ApiResult.success("验证通过", null);
        }
        return ApiResult.error(400, "验证码错误或已过期");
    }
}
