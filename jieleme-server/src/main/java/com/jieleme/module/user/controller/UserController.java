package com.jieleme.module.user.controller;

import com.jieleme.common.api.ApiResponse;
import com.jieleme.common.api.ErrorCode;
import com.jieleme.common.exception.BizException;
import com.jieleme.module.user.entity.User;
import com.jieleme.module.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser(HttpServletRequest request) {
        // 从request attribute中获取userId（由AuthInterceptor设置）
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未授权");
        }
        
        User user = userService.getById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.SYSTEM_ERROR, "用户不存在");
        }
        
        return ApiResponse.ok(user);
    }
}
