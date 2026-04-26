package com.jieleme.module.photo.service.impl;

import com.jieleme.module.generate.factory.AiGeneratorFactory;
import com.jieleme.module.photo.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private AiGeneratorFactory generatorFactory;


    // 从 application.yml 读取当前生效的渠道，例如：volcengine
    @Value("${ai.provider.active}")
    private String activeProvider;

    @Override
    public String uploadToOSS(MultipartFile file) {
        return "";
    }

    @Override
    public String createGenerateTask(String originImageUrl, String templateId) {
        return "";
    }

    @Override
    public void asyncCallVolcengine(String taskId, String originImageUrl, String templateId) {

    }

    @Override
    public Map<String, Object> getTaskStatus(String taskId) {
        return Map.of();
    }
}
