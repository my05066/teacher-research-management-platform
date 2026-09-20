package com.tit.university.service;

import java.util.Map;


/**
 *
 * DeepSeek AI 服务：封装与 DeepSeek 模型的交互能力
 */
public interface DeepSeekService {

    /**
     * 根据OCR识别的文字，判断证书类型并提取结构化数据
     */
    Map<String, Object> classifyAndExtract(String ocrText);



    /**
     * 通用聊天接口：传入完整 Prompt，由DeepSeek返回回答内容
     */
    String chat(String prompt);
}
