package com.brins.springbootconfigfile.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailProperties {

    // 发件人邮箱
    @Value("${email.user}")
    public String user;

    // 发件人邮箱授权码
    @Value("${email.code}")
    public String code;

    // 发件人邮箱对应的服务器域名
    @Value("${email.host}")
    public String host;

    // 身份验证开关
    @Value("${email.auth}")
    public boolean auth;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }
}
