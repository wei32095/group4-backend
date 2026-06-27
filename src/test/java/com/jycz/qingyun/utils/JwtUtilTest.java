package com.jycz.qingyun.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtils;

    @Test
    public void testGenerateAndParseToken() {
        // 1. 生成 Token（假设用户ID=1，角色=1学员）
        String token = jwtUtils.generateToken(1L, 1);
        System.out.println("✅ 生成的 Token:");
        System.out.println(token);

        // 2. 解析 Token，获取用户ID
        Long userId = jwtUtils.getUserIdFromToken(token);
        System.out.println("✅ 解析出的 userId: " + userId);

        // 3. 解析 Token，获取角色
        Integer role = jwtUtils.getRoleFromToken(token);
        System.out.println("✅ 解析出的 role: " + role);

        // 4. 验证 Token 是否有效
        boolean isValid = jwtUtils.validateToken(token);
        System.out.println("✅ Token 是否有效: " + isValid);
    }
}