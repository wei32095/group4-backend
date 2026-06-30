package com.jycz.qingyun.config;

import com.jycz.qingyun.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/qingyun/login",              // 密码登录
                        "/qingyun/login/mp",           // 小程序登录
                        "/qingyun/login/verify-code",  // 验证码登录
                        "/qingyun/register",           // 注册
                        "/qingyun/verify-code/**",     // 验证码（无需登录）
                        "/hello"                       // 健康检查
                );
    }
}
