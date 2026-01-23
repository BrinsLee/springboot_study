package com.jieleme.common.util;

import java.util.regex.Pattern;

/**
 * 手机号工具类
 */
public class PhoneUtil {
    
    // 中国大陆手机号正则（简化版：1开头的11位数字）
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");
    
    /**
     * 校验手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * 手机号脱敏显示
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
