package com.jieleme.module.auth.service.impl;

import com.jieleme.common.api.ErrorCode;
import com.jieleme.common.exception.BizException;
import com.jieleme.common.util.JwtUtil;
import com.jieleme.common.util.PhoneUtil;
import com.jieleme.module.auth.dto.LoginReq;
import com.jieleme.module.auth.dto.LoginResp;
import com.jieleme.module.auth.dto.SendCodeReq;
import com.jieleme.module.auth.service.AuthService;
import com.jieleme.module.sms.service.SmsCodeService;
import com.jieleme.module.user.entity.User;
import com.jieleme.module.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    @Autowired
    private SmsCodeService smsCodeService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${auth.token-expire-seconds}")
    private long tokenExpireSeconds;
    
    @Override
    public Map<String, Object> sendCode(SendCodeReq req, HttpServletRequest request) {
        String phone = req.getPhone();
        String scene = req.getScene();
        
        // 1. 校验手机号格式
        if (!PhoneUtil.isValidPhone(phone)) {
            throw new BizException(ErrorCode.INVALID_PHONE, "手机号格式不合法");
        }
        
        // 2. 获取请求IP
        String ip = getClientIp(request);
        
        // 3. 发送验证码
        return smsCodeService.sendCode(phone, scene, ip);
    }
    
    @Override
    public LoginResp login(LoginReq req) {
        String phone = req.getPhone();
        String code = req.getCode();
        
        // 1. 校验手机号格式
        if (!PhoneUtil.isValidPhone(phone)) {
            throw new BizException(ErrorCode.INVALID_PHONE, "手机号格式不合法");
        }
        
        // 2. 验证验证码
        boolean isValidCode = smsCodeService.verifyCode(phone, "login", code);
        if (!isValidCode) {
            throw new BizException(ErrorCode.INVALID_CODE, "验证码错误或已过期");
        }
        
        // 3. 查询或创建用户
        User user = userService.getByPhone(phone);
        if (user == null) {
            // 自动注册
            user = userService.createUser(phone);
            log.info("新用户自动注册: userId={}, phone={}", user.getId(), phone);
        } else {
            // 检查用户状态
            if (user.getStatus() != 1) {
                throw new BizException(ErrorCode.USER_DISABLED, "用户已被禁用");
            }
            log.info("用户登录: userId={}, phone={}", user.getId(), phone);
        }
        
        // 4. 更新最后登录时间
        userService.updateLastLogin(user.getId(), LocalDateTime.now());
        
        // 5. 标记验证码为已使用
        smsCodeService.markCodeAsUsed(phone, "login", code);
        
        // 6. 生成token
        String token = jwtUtil.generateToken(user.getId(), phone);
        String bearerToken = "Bearer " + token;
        
        // 7. 返回登录结果
        return new LoginResp(bearerToken, tokenExpireSeconds, user);
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个IP取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
