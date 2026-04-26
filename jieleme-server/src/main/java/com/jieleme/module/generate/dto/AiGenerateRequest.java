package com.jieleme.module.generate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiGenerateRequest {
    private String taskId;         // 内部任务单号
    private String userImageUrl;   // 用户上传的原图 URL
    private String templateUrl;    // 选定的模板底图 URL
    private String prompt;         // 可选：提示词（如果有）
}