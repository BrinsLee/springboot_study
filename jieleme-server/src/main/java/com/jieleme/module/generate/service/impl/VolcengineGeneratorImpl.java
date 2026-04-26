package com.jieleme.module.generate.service.impl;

import com.jieleme.module.generate.dto.AiGenerateRequest;
import com.jieleme.module.generate.dto.AiGenerateResponse;
import com.jieleme.module.generate.service.AiImageGenerator;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import com.volcengine.service.visual.model.request.VisualFaceSwapRequest;
import com.volcengine.service.visual.model.response.VisualFaceSwapResponse;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

// 火山引擎实现类
@Service
public class VolcengineGeneratorImpl implements AiImageGenerator {

    @Value("${ai.volcengine.ak}")
    private String accessKey;

    @Value("${ai.volcengine.sk}")
    private String secretKey;

    // 火山引擎视觉服务客户端
    private IVisualService visualService;

    /**
     * @PostConstruct 注解：在 Spring Bean 初始化完成后，自动组装鉴权客户端
     */
    @PostConstruct
    public void initClient() {
        visualService = VisualServiceImpl.getInstance();
        visualService.setAccessKey(accessKey);
        visualService.setSecretKey(secretKey);
    }

    @Override
    public String getProviderName() {
        return "volcengine";
    }

    @Override
    public AiGenerateResponse generateImage(AiGenerateRequest request) {

        try {
            // 2. 组装强类型的请求对象
            VisualFaceSwapRequest apiRequest = new VisualFaceSwapRequest();
            apiRequest.setImageUrl(request.getUserImageUrl());    // 用户人脸图
            apiRequest.setTemplateUrl(request.getTemplateUrl());  // 模板图
            apiRequest.setDoRisk(false); // 对应文档里的风控开关

            // 3. 执行具名调用
            // 注意：根据你下载的 SDK 小版本差异，方法名可能叫 faceSwap 或 aigcFaceSwap
            // 请在 IDE 中敲出 visualService. 后根据提示选择对应的方法
            VisualFaceSwapResponse apiResponse = visualService.faceSwap(apiRequest);

            // 4. 解析结果
            if (apiResponse != null && apiResponse.getCode() == 10000) {
                String resultUrl = apiResponse.getData().getImage();
                System.out.println("✅ 生图成功！结果URL: " + resultUrl);
                return AiGenerateResponse.builder()
                        .success(true)
                        .resultImageUrl(resultUrl)
                        .build();
            }

            // 业务层面的拦截（如未检测到人脸等）
            String errorMsg = apiResponse != null ? apiResponse.getMessage() : "API返回为空";
            System.err.println("❌ 引擎拒绝执行: " + errorMsg);
            return AiGenerateResponse.builder()
                    .success(false)
                    .errorMessage(errorMsg)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return AiGenerateResponse.builder()
                    .success(false)
                    .errorMessage("底层调用异常: " + e.getMessage())
                    .build();
        }
    }
}
