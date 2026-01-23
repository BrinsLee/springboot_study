package com.jieleme.module.sms.service.impl;

import com.jieleme.common.api.ErrorCode;
import com.jieleme.common.exception.BizException;
import com.jieleme.module.sms.entity.SmsCode;
import com.jieleme.module.sms.mapper.SmsCodeMapper;
import com.jieleme.module.sms.service.SmsCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 短信验证码服务实现
 */
@Service
public class SmsCodeServiceImpl implements SmsCodeService {
    
    private static final Logger log = LoggerFactory.getLogger(SmsCodeServiceImpl.class);
    
    private static final String TEST_CODE = "123456"; // 测试模式固定验证码
    
    @Autowired
    private SmsCodeMapper smsCodeMapper;
    
    @Value("${auth.test-mode:true}")
    private boolean testMode;
    
    @Value("${auth.sms-expire-seconds:300}")
    private int smsExpireSeconds;
    
    @Value("${auth.sms-min-interval-seconds:60}")
    private int smsMinIntervalSeconds;
    
    @Value("${auth.sms-max-per-hour:5}")
    private int smsMaxPerHour;
    
    @Override
    public Map<String, Object> sendCode(String phone, String scene, String ip) {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. 检查频率限制：60秒内不能重复发送
        SmsCode latestCode = smsCodeMapper.selectLatest(phone, scene);
        if (latestCode != null) {
            long secondsSinceLastSend = java.time.Duration.between(latestCode.getCreatedAt(), now).getSeconds();
            if (secondsSinceLastSend < smsMinIntervalSeconds) {
                throw new BizException(ErrorCode.CODE_TOO_FREQUENT, 
                    "验证码发送过于频繁，请" + (smsMinIntervalSeconds - secondsSinceLastSend) + "秒后重试");
            }
        }
        
        // 2. 检查1小时内发送次数
        LocalDateTime oneHourAgo = now.minusHours(1);
        int countInLastHour = smsCodeMapper.countInTimeRange(phone, scene, oneHourAgo, now);
        if (countInLastHour >= smsMaxPerHour) {
            throw new BizException(ErrorCode.CODE_TOO_FREQUENT, 
                "验证码发送次数超限，请1小时后重试");
        }
        
        // 3. 生成验证码和requestId
        String code = testMode ? TEST_CODE : generateRandomCode();
        String requestId = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expireAt = now.plusSeconds(smsExpireSeconds);
        
        // 4. 保存到数据库
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setScene(scene);
        smsCode.setRequestId(requestId);
        smsCode.setCode(code);
        smsCode.setExpireAt(expireAt);
        smsCode.setCreatedAt(now);
        smsCode.setIp(ip);
        
        int rows = smsCodeMapper.insert(smsCode);
        if (rows <= 0) {
            throw new BizException(ErrorCode.SYSTEM_ERROR, "发送验证码失败");
        }
        
        log.info("发送验证码成功: phone={}, scene={}, requestId={}, code={}", 
            phone, scene, requestId, testMode ? code : "******");
        
        // 5. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("requestId", requestId);
        result.put("expireSeconds", smsExpireSeconds);
        
        return result;
    }
    
    @Override
    public boolean verifyCode(String phone, String scene, String code) {
        LocalDateTime now = LocalDateTime.now();
        
        // 测试模式：固定验证码123456
        if (testMode && TEST_CODE.equals(code)) {
            log.debug("测试模式验证码校验通过: phone={}", phone);
            return true;
        }
        
        // 查询最新的有效验证码
        SmsCode smsCode = smsCodeMapper.selectLatestValid(phone, scene, now);
        if (smsCode == null) {
            log.warn("验证码不存在或已过期: phone={}, scene={}", phone, scene);
            return false;
        }
        
        // 校验验证码
        boolean isValid = code.equals(smsCode.getCode());
        if (isValid) {
            log.info("验证码校验通过: phone={}, scene={}", phone, scene);
        } else {
            log.warn("验证码错误: phone={}, scene={}, inputCode={}", phone, scene, code);
        }
        
        return isValid;
    }
    
    @Override
    public void markCodeAsUsed(String phone, String scene, String code) {
        LocalDateTime now = LocalDateTime.now();
        SmsCode smsCode = smsCodeMapper.selectLatestValid(phone, scene, now);
        if (smsCode != null && code.equals(smsCode.getCode())) {
            smsCodeMapper.markAsUsed(smsCode.getId(), now);
            log.debug("标记验证码为已使用: id={}", smsCode.getId());
        }
    }
    
    /**
     * 生成6位随机数字验证码
     */
    private String generateRandomCode() {
        int code = (int) ((Math.random() * 900000) + 100000);
        return String.valueOf(code);
    }
}
