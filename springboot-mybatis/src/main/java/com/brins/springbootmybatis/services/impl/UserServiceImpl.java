package com.brins.springbootmybatis.services.impl;

import com.brins.springbootmybatis.mapper.UserMapper;
import com.brins.springbootmybatis.pojo.User;
import com.brins.springbootmybatis.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lipeilin on 2024/10/3.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);

    }
}
