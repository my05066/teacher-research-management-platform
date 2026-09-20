package com.tit.university.mapper;

import com.tit.university.pojo.AiAssistantChat;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AiAssistantChatMapper {
    /**
     * 根据用户ID查询聊天记录
     */
    @Select("select id, user_id userId, role, content, create_time createTime, update_time updateTime " +
            "from ai_assistant_chat where user_id = #{userId} order by create_time asc")
    List<AiAssistantChat> findByUserId(@Param("userId") Long userId);

    /**
     * 批量插入聊天记录
     */
    void batchInsert(@Param("chats") List<AiAssistantChat> chats);



    /**
     * 根据用户ID删除聊天记录
     */
    @Delete("delete from ai_assistant_chat where user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);
}