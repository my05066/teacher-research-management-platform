package com.tit.university.mapper;

import com.tit.university.pojo.Essay;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EssayMapper {

    /**
     *
     * 分页查询(包含用户信息i)
     */
    List<Essay> list(Long userId, String status);



    /**
     * 添加论文
     */
    void insert(Essay essay);


    /**
     * 根据id查询
     */
    @Select("select id, user_id userId, title, level, author_type authorType, status, " +
            "publish_time publishTime, journal_name journalName, certificate_path certificatePath, " +
            "certificate_name certificateName, certificate_size certificateSize, audit_user_id auditUserId, " +
            "audit_time auditTime, audit_remark auditRemark, create_time createTime, " +
            "update_time updateTime, delete_flag deleteFlag from essay where id = #{id} and delete_flag = 0")
    Essay selectById(Long id);

    /**
     * 更新审核信息
     */
    @Update("update essay set status = #{status}, audit_user_id = #{auditUserId}, " +
            "audit_time = #{auditTime}, audit_remark = #{auditRemark} where id = #{id}")
    void updateAudit(Essay essay);




    /**
     * 删除 ( 逻辑删除)
     */
    @Update("UPDATE essay SET delete_flag = 1 WHERE id = #{id}")
    void deleteById(Long id);





    /**
     * 统计数量
     *
     */
    long countByUserId(Long userId, String status);
}

