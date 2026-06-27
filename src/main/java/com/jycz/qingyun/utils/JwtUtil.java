package com.jycz.qingyun.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private Long expire;

    /**
     * 生成 JWT Token
     * @param userId 用户ID
     * @param role 用户角色（1-学员，2-讲师，3-管理员）
     * @return JWT 字符串
     */
    public String generateToken(Long userId, Integer role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire);

        return JWT.create()
                .withClaim("userId", userId)   // 存入用户ID
                .withClaim("role", role)       // 存入角色
                .withIssuedAt(now)             // 签发时间
                .withExpiresAt(expireDate)     // 过期时间
                .sign(Algorithm.HMAC256(secret)); // 使用 HMAC256 算法签名
    }

    /**
     * 解析 JWT Token，获取用户ID
     * @param token JWT 字符串
     * @return 用户ID
     * @throws JWTVerificationException 如果 Token 无效或过期
     */
    public Long getUserIdFromToken(String token) throws JWTVerificationException {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
        return decodedJWT.getClaim("userId").asLong();
    }

    /**
     * 解析 JWT Token，获取用户角色
     * @param token JWT 字符串
     * @return 角色（1-学员，2-讲师，3-管理员）
     * @throws JWTVerificationException 如果 Token 无效或过期
     */
    public Integer getRoleFromToken(String token) throws JWTVerificationException {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
        return decodedJWT.getClaim("role").asInt();
    }

    /**
     * 验证 Token 是否有效
     * @param token JWT 字符串
     * @return true-有效，false-无效/过期
     */
    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}