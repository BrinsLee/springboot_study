package com.jieleme.module.sms.mapper;

import com.jieleme.module.sms.entity.SmsCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 短信验证码Mapper接口
 */
@Mapper
public interface SmsCodeMapper {
    
    /**
     * 插入验证码记录
     */
    int insert(SmsCode smsCode);
    
    /**
     * 查询最新的有效验证码
     */
    SmsCode selectLatestValid(@Param("phone") String phone, 
                              @Param("scene") String scene, 
                              @Param("now") LocalDateTime now);
    
    /**
     * 统计最近一段时间内的发送次数
     */
    int countInTimeRange(@Param("phone") String phone, 
                         @Param("scene") String scene, 
                         @Param("fromTime") LocalDateTime fromTime,
                         @Param("toTime") LocalDateTime toTime);
    
    /**
     * 查询最新的一条记录（不管是否过期）
     */
    SmsCode selectLatest(@Param("phone") String phone, 
                         @Param("scene") String scene);
    
    /**
     * 标记验证码为已使用
     */
    int markAsUsed(@Param("id") Long id, @Param("usedAt") LocalDateTime usedAt);
}
