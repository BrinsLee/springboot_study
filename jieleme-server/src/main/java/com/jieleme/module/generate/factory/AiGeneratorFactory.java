package com.jieleme.module.generate.factory;

import com.jieleme.module.generate.service.AiImageGenerator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AiGeneratorFactory implements InitializingBean {

    // 注入所有的实现类
    @Autowired
    private List<AiImageGenerator> generators;

    // 按 providerName 缓存映射表
    private Map<String, AiImageGenerator> generatorMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() {
        for (AiImageGenerator generator : generators) {
            generatorMap.put(generator.getProviderName(), generator);
        }
    }

    /**
     * 获取指定的生图执行器
     */
    public AiImageGenerator getGenerator(String providerName) {
        AiImageGenerator generator = generatorMap.get(providerName);
        if (generator == null) {
            throw new IllegalArgumentException("未找到对应的 AI 生图渠道: " + providerName);
        }
        return generator;
    }
}
