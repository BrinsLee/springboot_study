package com.jieleme.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jieleme.common.api.Result;
import com.jieleme.common.api.ErrorCode;
import com.jieleme.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取Authorization header
        String authorization = request.getHeader("Authorization");
        
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("未提供有效的Authorization header: uri={}", request.getRequestURI());
            writeUnauthorizedResponse(response, "未授权，请先登录");
            return false;
        }
        
        // 提取token
        String token = authorization.substring(7);
        
        // 验证token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token验证失败: uri={}", request.getRequestURI());
            writeUnauthorizedResponse(response, "Token无效或已过期");
            return false;
        }
        
        // 从token中获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(token);
        String phone = jwtUtil.getPhoneFromToken(token);
        
        if (userId == null) {
            log.warn("无法从token中获取用户信息: uri={}", request.getRequestURI());
            writeUnauthorizedResponse(response, "Token无效");
            return false;
        }
        
        // 将用户信息放入request attribute，供后续使用
        request.setAttribute("userId", userId);
        request.setAttribute("phone", phone);
        
        log.debug("认证通过: userId={}, phone={}, uri={}", userId, phone, request.getRequestURI());
        return true;
    }
    
    /**
     * 写入未授权响应
     */
    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<?> result = Result.fail(ErrorCode.UNAUTHORIZED, message);
        String json = objectMapper.writeValueAsString(result);
        
        response.getWriter().write(json);
    }
}
