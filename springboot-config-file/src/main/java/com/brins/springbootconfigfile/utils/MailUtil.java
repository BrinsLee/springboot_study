package com.brins.springbootconfigfile.utils;

import com.brins.springbootconfigfile.pojo.EmailProperties;

public class MailUtil {

    /**
     * 发送邮件
     * @param emailProperties 发件人信息（发件人邮箱，授权码）及邮件服务器信息（邮件服务器域名，身份验证开关）
     * @param to 收件人
     * @param title 标题
     * @param content 邮件正文
     * @return
     */
    public static boolean sendMail(EmailProperties emailProperties , String to, String title, String content) {
        return false;
    }
}
