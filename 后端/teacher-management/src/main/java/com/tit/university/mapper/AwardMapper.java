package com.tit.university.mapper;

import com.tit.university.pojo.Award;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AwardMapper {

    /**
     * 分页查询（包含用户信息）
     */
    List<Award> list(Long userId, String status);




    /**
     * 添加获奖
     */
    @Insert("insert into award (user_id, award_name, level, award_time, status, award_org, certificate_path, certificate_name, certificate_size) " +
            "values (#{userId}, #{awardName}, #{level}, #{awardTime}, #{status}, #{awardOrg}, #{certificatePath}, #{certificateName}, #{certificateSize})")
    void insert(Award award);


    /**
     * 根据ID查询
     */
    @Select("select id, user_id userId, award_name awardName, level, award_time awardTime, status, " +
            "award_org awardOrg, certificate_path certificatePath, certificate_name certificateName, " +
            "certificate_size certificateSize, audit_user_id auditUserId, audit_time auditTime, " +
            "audit_remark auditRemark, create_time createTime, update_time updateTime, delete_flag deleteFlag " +
            "from award where id = #{id} and delete_flag = 0")
    Award selectById(Long id);



    /**
     * 更新审核信息
     */
    @Update("update award set status = #{status}, audit_user_id = #{auditUserId}, " +
            "audit_time = #{auditTime}, audit_remark = #{auditRemark} where id = #{id}")
    void updateAudit(Award award);


    /**
     * 删除（逻辑删除）
     */
    @Update("update award set delete_flag = 1 where id = #{id}")
    void deleteById(Long id);




    /**
     *
     * 统计数量
     */
    long countByUserId(Long userId, String status);
}

