package com.brins.springbootconfigfile.service.impl;

import com.brins.springbootconfigfile.pojo.EmailProperties;
import com.brins.springbootconfigfile.service.EmailService;
import com.brins.springbootconfigfile.utils.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailProperties emailProperties;


    /**
     *
     * @param to 收件人
     * @param title 邮件标题
     * @param content 邮件正文
     * @return
     */
    @Override
    public boolean sendMail(String to, String title, String content) {
        // 打印email配置信息
        System.out.println("email.host:" + emailProperties.getHost());
        System.out.println("email.user:" + emailProperties.getUser());
        System.out.println("email.code:" + emailProperties.getCode());
        System.out.println("email.auth:" + emailProperties.isAuth());
        return MailUtil.sendMail(emailProperties, to, title, content);
    }
}
