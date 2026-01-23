package com.jieleme.module.user.service.impl;

import com.jieleme.common.api.ErrorCode;
import com.jieleme.common.exception.BizException;
import com.jieleme.common.util.NicknameGenerator;
import com.jieleme.module.user.entity.User;
import com.jieleme.module.user.mapper.UserMapper;
import com.jieleme.module.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User getByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }
    
    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
    
    @Override
    public User createUser(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickname(NicknameGenerator.generate());
        user.setStatus(1); // 正常状态
        
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setLastLoginAt(now);
        
        int rows = userMapper.insert(user);
        if (rows <= 0) {
            throw new BizException(ErrorCode.SYSTEM_ERROR, "创建用户失败");
        }
        
        log.info("创建新用户成功: id={}, phone={}, nickname={}", user.getId(), phone, user.getNickname());
        return user;
    }
    
    @Override
    public void updateLastLogin(Long userId, LocalDateTime loginTime) {
        userMapper.updateLastLogin(userId, loginTime);
    }
}
