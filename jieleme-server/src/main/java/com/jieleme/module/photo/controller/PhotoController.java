package com.jieleme.module.photo.controller;

import com.jieleme.common.api.Result;
import com.jieleme.module.photo.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/photo")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    /**
     * 接收前端上传的照片并创建生图任务
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadAndGenerate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("templateId") String templateId) {

        // 1. 基础校验
        if (file.isEmpty()) {
            return Result.fail(400, "上传的照片不能为空");
        }

        // 可选：校验 templateId 是否合法，校验用户余额等

        try {
            // 2. 将照片上传到对象存储 (OSS/COS)，获取公网可访问的 URL
            // 注意：火山引擎的大部分图像 API 都要求传入图片的 URL，而不是直接传二进制
            String originImageUrl = photoService.uploadToOSS(file);

            // 3. 在数据库中创建一条生图任务，状态为“排队中/生成中”
            String taskId = photoService.createGenerateTask(originImageUrl, templateId);

            // 4. 触发异步调用火山引擎 API 进行 AI 生图
            // 这是一个非阻塞方法，会交给 Spring 的线程池去跑
            photoService.asyncCallVolcengine(taskId, originImageUrl, templateId);

            // 5. 立即将任务 ID 返回给前端，前端拿着这个 ID 去轮询进度
            Map<String, String> responseData = new HashMap<>();
            responseData.put("taskId", taskId);
            return Result.success(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500, "任务创建失败：" + e.getMessage());
        }
    }

    /**
     * 前端轮询获取任务状态的接口
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> getTaskStatus(@RequestParam("taskId") String taskId) {
        // 从数据库查询任务状态和结果
        Map<String, Object> statusInfo = photoService.getTaskStatus(taskId);
        return Result.success(statusInfo);
    }
}
