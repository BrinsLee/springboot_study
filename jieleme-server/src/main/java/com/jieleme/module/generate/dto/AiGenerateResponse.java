package com.jieleme.module.generate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiGenerateResponse {
    private boolean success;       // 是否调用成功
    private String resultImageUrl; // 生成的最终图片 URL
    private String errorMessage;   // 失败时的错误信息
}
