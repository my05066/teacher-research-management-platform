package com.tit.university.mapper;

import com.tit.university.pojo.Program;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProgramMapper {

    /**
     * 分页查询（包含用户信息）
     */
    List<Program> list(Long userId, String status);

    /**
     * 添加项目
     */
    void insert(Program program);

    /**
     * 根据ID查询
     */
    Program selectById(Long id);

    /**
     * 更新审核信息
     */
    @Update("update research_project set status = #{status}, audit_user_id = #{auditUserId}, " +
            "audit_time = #{auditTime}, audit_remark = #{auditRemark} where id = #{id}")
    void updateAudit(Program program);

    /**
     * 更新结题材料（横向项目）
     */
    @Update("update research_project set completion_doc_path = #{completionDocPath}, " +
            "completion_doc_name = #{completionDocName}, completion_doc_size = #{completionDocSize}, " +
            "is_completed = #{isCompleted}, status = #{status} where id = #{id}")
    void updateCompletionDoc(Program program);

    /**
     * 删除（逻辑删除）
     */
    @Update("update research_project set delete_flag = 1 where id = #{id}")
    void deleteById(Long id);

    /**
     * 统计数量
     */
    long countByUserId(Long userId, String status);

    /**
     * 统计在研项目（横向项目且未完成）
     */
    long countOngoingProjects(Long userId);
}

