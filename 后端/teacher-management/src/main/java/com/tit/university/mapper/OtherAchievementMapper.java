package com.tit.university.mapper;

import com.tit.university.pojo.OtherAchievement;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OtherAchievementMapper {

    /**
     * 分页查询（包含用户信息）
     */
    List<OtherAchievement> list(Long userId, String status);



    /**
     * 添加其他成果
     */
    @Insert("insert into other_achievement (user_id, title, description, status, evidence_path, evidence_name, evidence_size) " +
            "values (#{userId}, #{title}, #{description}, #{status}, #{evidencePath}, #{evidenceName}, #{evidenceSize})")
    void insert(OtherAchievement otherAchievement);



    /**
     * 根据id查询
     */
    @Select("select id, user_id userId, title, description, status, evidence_path evidencePath, " +
            "evidence_name evidenceName, evidence_size evidenceSize, audit_user_id auditUserId, " +
            "audit_time auditTime, audit_remark auditRemark, create_time createTime, " +
            "update_time updateTime, delete_flag deleteFlag " +
            "from other_achievement where id = #{id} and delete_flag = 0")
    OtherAchievement selectById(Long id);


    /**
     * 更新审核信息
     */
    @Update("update other_achievement set status = #{status}, audit_user_id = #{auditUserId}, " +
            "audit_time = #{auditTime}, audit_remark = #{auditRemark} where id = #{id}")
    void updateAudit(OtherAchievement otherAchievement);

    /**
     * 删除（逻辑删除）
     */
    @Update("update other_achievement set delete_flag = 1 where id = #{id}")
    void deleteById(Long id);


    /**
     * 统计数量
     */
    long countByUserId(Long userId, String status);



}



