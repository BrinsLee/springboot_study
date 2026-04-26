package com.jieleme.module.generate.service;

import com.jieleme.module.generate.dto.AiGenerateRequest;
import com.jieleme.module.generate.dto.AiGenerateResponse;

public interface AiImageGenerator {

    /**
     * 标识当前实现类对应的渠道名 (如：volcengine, aliyun, siliconflow)
     */
    String getProviderName();

    /**
     * 执行具体的生图逻辑
     */
    AiGenerateResponse generateImage(AiGenerateRequest request);
}
