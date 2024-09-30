package com.brins.springbootconfigfile.controller;

import com.brins.springbootconfigfile.pojo.EmailProperties;
import com.brins.springbootconfigfile.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {


    @Autowired
    private EmailService emailService;

    @RequestMapping("/send")
    public Boolean sendMail() {
        String to = "15817976978@163.com";
        String title = "测试邮件";
        String content = "测试邮件内容";
        return emailService.sendMail(to, title, content);
    }

}
