package com.jieleme.module.auth.service;

import com.jieleme.module.auth.dto.LoginReq;
import com.jieleme.module.auth.dto.LoginResp;
import com.jieleme.module.auth.dto.SendCodeReq;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 发送验证码
     */
    Map<String, Object> sendCode(SendCodeReq req, HttpServletRequest request);
    
    /**
     * 登录（自动注册）
     */
    LoginResp login(LoginReq req);
}
