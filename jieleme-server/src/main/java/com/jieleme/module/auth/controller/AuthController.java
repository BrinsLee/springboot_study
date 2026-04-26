package com.jieleme.module.auth.controller;

import com.jieleme.common.api.Result;
import com.jieleme.module.auth.dto.LoginReq;
import com.jieleme.module.auth.dto.LoginResp;
import com.jieleme.module.auth.dto.SendCodeReq;
import com.jieleme.module.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 发送验证码
     */
    @PostMapping("/sendCode")
    public Result<Map<String, Object>> sendCode(@Valid @RequestBody SendCodeReq req,
                                                HttpServletRequest request) {
        Map<String, Object> result = authService.sendCode(req, request);
        return Result.success(result);
    }
    
    /**
     * 登录（自动注册）
     */
    @PostMapping("/login")
    public Result<LoginResp> login(@Valid @RequestBody LoginReq req) {
        LoginResp result = authService.login(req);
        return Result.success(result);
    }
}
