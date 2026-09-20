package com.tit.university.controller;

import com.tit.university.pojo.AiAnswer;
import com.tit.university.pojo.AiQuestion;
import com.tit.university.pojo.Result;
import com.tit.university.pojo.AiAssistantChat;
import com.tit.university.service.AiAssistantChatService;
import com.tit.university.service.AiAssistantService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 研觉晓AI助手接口
@Slf4j
@RestController
@RequestMapping("/ai")
public class AiAssistantController {

    @Autowired
    private AiAssistantService aiAssistantService;
    
    @Autowired
    private AiAssistantChatService aiAssistantChatService;

    // 提问
    @PostMapping("/ask")
    public Result<Map<String, String>> ask(@RequestBody Map<String, String> body, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("研觉晓提问, userId={}", userId);
        if (userId == null) {
            return Result.error("未获取到用户信息，请重新登录");
        }
        String q = body.get("question");
        if (q == null || q.isBlank()) {
            return Result.error("问题不能为空");
        }
        // 保存用户问题
        AiAssistantChat userChat = new AiAssistantChat();
        userChat.setUserId(userId);
        userChat.setRole("user");
        userChat.setContent(q);
        aiAssistantChatService.saveChats(List.of(userChat));
        
        String answer = aiAssistantService.ask(userId, q);
        
        // 保存AI回答
        AiAssistantChat aiChat = new AiAssistantChat();
        aiChat.setUserId(userId);
        aiChat.setRole("assistant");
        aiChat.setContent(answer);
        aiAssistantChatService.saveChats(List.of(aiChat));
        
        return Result.success(Map.of("answer", answer));
    }

    // 查聊天记录
    @GetMapping("/chat-history")
    public Result<List<AiAssistantChat>> getHistory(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("获取聊天记录 userId={}", userId);
        if (userId == null) {
            return Result.error("未获取到用户信息，请重新登录");
        }
        return Result.success(aiAssistantChatService.getChatsByUserId(userId));
    }


    // 保存聊天记录
    @PostMapping("/chat-history")
    public Result saveHistory(@RequestBody List<AiAssistantChat> chatList, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("保存聊天记录 userId={}, count={}", userId, chatList != null ? chatList.size() : 0);
        if (userId == null) {
            return Result.error("未获取到用户信息，请重新登录");
        }
        if (chatList != null) {
            for (AiAssistantChat chat : chatList) {
                chat.setUserId(userId);
            }
        }
        aiAssistantChatService.saveChats(chatList);
        return Result.success();
    }

    // 清除聊天记录
    @DeleteMapping("/chat-history")
    public Result clearHistory(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("清除聊天记录 userId={}", userId);
        if (userId == null) {
            return Result.error("未获取到用户信息，请重新登录");
        }
        aiAssistantChatService.clearChatsByUserId(userId);
        return Result.success();
    }
}