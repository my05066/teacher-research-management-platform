package com.tit.university.service.impl;

import com.tit.university.mapper.AiAssistantChatMapper;
import com.tit.university.pojo.AiAssistantChat;
import com.tit.university.service.AiAssistantChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiAssistantChatServiceImpl implements AiAssistantChatService {

    @Autowired
    private AiAssistantChatMapper aiAssistantChatMapper;


    @Override
    public List<AiAssistantChat> getChatsByUserId(Long userId) {
        return aiAssistantChatMapper.findByUserId(userId);
    }

    @Override
    public void saveChats(List<AiAssistantChat> chats) {
        if (chats != null && !chats.isEmpty()) {
            aiAssistantChatMapper.batchInsert(chats);
        }
    }

    @Override
    public void clearChatsByUserId(Long userId) {
        aiAssistantChatMapper.deleteByUserId(userId);
    }
}