package com.jieleme.module.photo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PhotoService {

    /**
     * 上传到oss
     * @param file
     * @return
     */
    public String uploadToOSS(MultipartFile file);


    /**
     * 创建生成任务
     * @param originImageUrl
     * @param templateId
     * @return
     */
    public String createGenerateTask(String originImageUrl, String templateId);


    /**
     * 核心异步生图方法
     * @Async 表明该方法会在独立的线程池中执行，不会阻塞主线程
     */
    @Async
    public void asyncCallVolcengine(String taskId, String originImageUrl, String templateId);


    /**
     * 查询任务状态
     * @param taskId
     * @return
     */
    public Map<String, Object> getTaskStatus(String taskId);
}
