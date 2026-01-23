package com.jieleme.module.user.service;

import com.jieleme.module.user.entity.User;

import java.time.LocalDateTime;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 根据手机号查询用户
     */
    User getByPhone(String phone);
    
    /**
     * 根据ID查询用户
     */
    User getById(Long id);
    
    /**
     * 创建新用户
     */
    User createUser(String phone);
    
    /**
     * 更新最后登录时间
     */
    void updateLastLogin(Long userId, LocalDateTime loginTime);
}
