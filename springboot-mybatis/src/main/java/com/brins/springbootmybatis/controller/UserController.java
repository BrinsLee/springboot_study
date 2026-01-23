package com.brins.springbootmybatis.controller;

import com.brins.springbootmybatis.pojo.User;
import com.brins.springbootmybatis.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lipeilin on 2024/10/3.
 */

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/findById")
    public User findById(Integer id) {
        return userService.findById(id);
    }
}
