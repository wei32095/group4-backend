package com.jycz.qingyun.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置
 * 使用 BCrypt 加盐哈希，自动处理盐值，防御彩虹表攻击
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 强度 10（默认），越高越安全但越慢，10 是安全与性能的合理折中
        return new BCryptPasswordEncoder(10);
    }
}
