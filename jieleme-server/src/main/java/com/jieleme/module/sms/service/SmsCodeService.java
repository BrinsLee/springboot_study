package com.jieleme.module.sms.service;

import com.jieleme.module.sms.entity.SmsCode;

import java.util.Map;

/**
 * 短信验证码服务接口
 */
public interface SmsCodeService {
    
    /**
     * 发送验证码
     * @param phone 手机号
     * @param scene 场景（login/register等）
     * @param ip 请求IP
     * @return 包含requestId和expireSeconds的Map
     */
    Map<String, Object> sendCode(String phone, String scene, String ip);
    
    /**
     * 验证验证码
     * @param phone 手机号
     * @param scene 场景
     * @param code 验证码
     * @return 验证是否通过
     */
    boolean verifyCode(String phone, String scene, String code);
    
    /**
     * 标记验证码为已使用
     */
    void markCodeAsUsed(String phone, String scene, String code);
}
