package com.jieleme.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {
    
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    
    @Value("${auth.jwt-secret}")
    private String secret;
    
    @Value("${auth.token-expire-seconds}")
    private long expireSeconds;
    
    /**
     * 生成JWT token
     * @param userId 用户ID
     * @param phone 手机号
     * @return token字符串
     */
    public String generateToken(Long userId, String phone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("phone", phone);
        
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireSeconds * 1000);
        
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        String token = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(key)
                .compact();
        
        log.debug("生成JWT token: userId={}, phone={}, expireAt={}", userId, phone, expireDate);
        return token;
    }
    
    /**
     * 解析JWT token
     * @param token token字符串
     * @return Claims对象，包含用户信息
     */
    public Claims parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.warn("解析JWT token失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object uid = claims.get("uid");
        if (uid instanceof Integer) {
            return ((Integer) uid).longValue();
        }
        return (Long) uid;
    }
    
    /**
     * 从token中获取手机号
     */
    public String getPhoneFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? (String) claims.get("phone") : null;
    }
    
    /**
     * 验证token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims != null && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
