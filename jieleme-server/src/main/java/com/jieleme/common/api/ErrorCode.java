package com.jieleme.common.api;

/**
 * 错误码常量定义
 */
public class ErrorCode {
    
    /** 成功 */
    public static final int SUCCESS = 0;
    
    /** 手机号格式不合法 */
    public static final int INVALID_PHONE = 1001;
    
    /** 验证码错误 */
    public static final int INVALID_CODE = 1002;
    
    /** 验证码过期 */
    public static final int CODE_EXPIRED = 1003;
    
    /** 验证码请求过于频繁 */
    public static final int CODE_TOO_FREQUENT = 1004;
    
    /** 用户被禁用 */
    public static final int USER_DISABLED = 1005;
    
    /** 系统异常 */
    public static final int SYSTEM_ERROR = 2000;
    
    /** 未授权 */
    public static final int UNAUTHORIZED = 4010;
    
    /** 参数错误 */
    public static final int INVALID_PARAM = 4000;
}
