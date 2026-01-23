package com.jieleme.module.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 发送验证码请求
 */
public class SendCodeReq {
    
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    @NotBlank(message = "场景不能为空")
    private String scene;
    
    // Getters and Setters
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getScene() {
        return scene;
    }
    
    public void setScene(String scene) {
        this.scene = scene;
    }
}
