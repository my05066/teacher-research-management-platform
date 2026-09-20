package com.tit.university.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

// 研觉晓 ai聊天记录类
@Data
public class AiAssistantChat {

    private Long id;

    private Long userId;
    // “user ” 或 ”assistant“
    private String role;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}