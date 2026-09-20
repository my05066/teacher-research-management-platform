package com.tit.university.mapper;

import com.tit.university.pojo.Patent;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PatentMapper {



    /**
     * 分页查询（包含用户信息）
     */
    List<Patent> list(Long userId, String status);



    /**
     * 添加专利
     */
    @Insert("insert into patent (user_id, name, type, approval_time, status, patent_number, certificate_path, certificate_name, certificate_size) " +
            "VALUES (#{userId}, #{name}, #{type}, #{approvalTime}, #{status}, #{patentNumber}, #{certificatePath}, #{certificateName}, #{certificateSize})")
    void insert(Patent patent);


    /**
     * 根据ID查询
     */
    @Select("select id, user_id userId, name, type, approval_time approvalTime, status, " +
            "patent_number patentNumber, certificate_path certificatePath, certificate_name certificateName, " +
            "certificate_size certificateSize, audit_user_id auditUserId, audit_time auditTime, " +
            "audit_remark auditRemark, create_time createTime, update_time updateTime, " +
            "delete_flag deleteFlag " +
            "from patent where id = #{id} and delete_flag = 0")
    Patent selectById(Long id);


    /**
     * 更新审核信息
     */
    @Update("update patent set status = #{status}, audit_user_id = #{auditUserId}, " +
            "audit_time = #{auditTime}, audit_remark = #{auditRemark} where id = #{id}")
    void updateAudit(Patent patent);


    /**
     * 删除（逻辑删除）
     */
    @Update("update patent set delete_flag = 1 where id = #{id}")
    void deleteById(Long id);





    /**
     * 统计数量
     */
    long countByUserId(Long userId, String status);
}

