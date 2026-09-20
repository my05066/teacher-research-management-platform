package com.tit.university.service;

/**
 * 研觉晓 AI 助手服务：面向当前登录教师，结合数据库中的科研成果数据进行智能问答与分析
 */
public interface AiAssistantService {

    /**
     * 基于当前用户的科研成果数据，回答用户自然语言问题
     *
     * @param userId  当前登录用户ID
     * @param question 用户提问
     * @return DeepSeek 返回的中文自然语言回答
     */
    String ask(Long userId, String question);
}

