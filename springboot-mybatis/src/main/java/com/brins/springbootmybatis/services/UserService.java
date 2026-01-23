package com.brins.springbootmybatis.services;

import com.brins.springbootmybatis.pojo.User;

/**
 * Created by lipeilin on 2024/10/3.
 */
public interface UserService {

    public User findById(Integer id);

}
