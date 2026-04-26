package com.jieleme.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一API响应包装类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    
    private int code;
    private String msg;
    private T data;
    
    public Result() {
    }
    
    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "ok", data);
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(0, "ok", null);
    }
    
    /**
     * 失败响应
     */
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }
    
    /**
     * 失败响应（带数据）
     */
    public static <T> Result<T> fail(int code, String msg, T data) {
        return new Result<>(code, msg, data);
    }
    
    // Getters and Setters
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}
