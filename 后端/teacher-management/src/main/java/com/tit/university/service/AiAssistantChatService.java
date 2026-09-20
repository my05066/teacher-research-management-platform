package com.tit.university.service;

import com.tit.university.pojo.AiAssistantChat;

import java.util.List;

public interface AiAssistantChatService {
    /**
     * 根据用户ID获取聊天记录
     */
    List<AiAssistantChat> getChatsByUserId(Long userId);

    /**
     * 保存聊天记录
     */
    void saveChats(List<AiAssistantChat> chats);

    /**
     * 清空指定用户的聊天记录
     */
    void clearChatsByUserId(Long userId);
}