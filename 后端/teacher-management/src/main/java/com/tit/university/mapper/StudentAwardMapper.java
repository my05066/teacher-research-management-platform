package com.tit.university.mapper;

import com.tit.university.pojo.StudentAward;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentAwardMapper {




    /**
     * 分页查询（包含用户信息）
     */
    List<StudentAward> list(Long userId, String status);



    /**
     * 添加学生获奖
     */
    @Insert("insert into student_award (user_id, student_name, award_name, award_type, award_time, status) " +
            "values (#{userId}, #{studentName}, #{awardName}, #{awardType}, #{awardTime}, #{status})")
    void insert(StudentAward studentAward);

    /**
     * 根据ID查询
     */
    @Select("select id, user_id userId, student_name studentName, award_name awardName, " +
            "award_type awardType, award_time awardTime, status, audit_user_id auditUserId, " +
            "audit_time auditTime, audit_remark auditRemark, create_time createTime, " +
            "update_time updateTime, delete_flag deleteFlag " +
            "from student_award where id = #{id} and delete_flag = 0")
    StudentAward selectById(Long id);



    /**
     * 更新审核信息
     */
    @Update("update student_award set status = #{status}, audit_user_id = #{auditUserId}, " +
            "audit_time = #{auditTime}, audit_remark = #{auditRemark} where id = #{id}")
    void updateAudit(StudentAward studentAward);

    /**
     *
     * 删除（逻辑删除）
     */
    @Update("update student_award set delete_flag = 1 where id = #{id}")
    void deleteById(Long id);



    /**
     * 统计数量
     */
    long countByUserId(Long userId, String status);
}

