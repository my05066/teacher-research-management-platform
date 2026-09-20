package com.tit.university.mapper;

import com.tit.university.pojo.Meeting;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MeetingMapper {

    /**
     * 分页查询（包含用户信息）
     */
    List<Meeting> list(Long userId, String status);


    /**
     * 添加会议
     */
    @Insert("insert into meeting (user_id, meeting_name, category, has_report, report_time, " +
            "meeting_time, meeting_location, status, evidence_path, evidence_name, evidence_size) " +
            "values (#{userId}, #{meetingName}, #{category}, #{hasReport}, #{reportTime}, " +
            "#{meetingTime}, #{meetingLocation}, #{status}, #{evidencePath}, #{evidenceName}, #{evidenceSize})")
    void insert(Meeting meeting);


    /**
     * 根据ID查询
     */
    @Select("select id, user_id userId, meeting_name meetingName, category, has_report hasReport, " +
            "report_time reportTime, meeting_time meetingTime, meeting_location meetingLocation, status, " +
            "evidence_path evidencePath, evidence_name evidenceName, evidence_size evidenceSize, " +
            "audit_user_id auditUserId, audit_time auditTime, audit_remark auditRemark, " +
            "create_time createTime, update_time updateTime, delete_flag deleteFlag " +
            "from meeting where id = #{id} and delete_flag = 0")
    Meeting selectById(Long id);


    /**
     * 更新审核信息
     */
    @Update("update meeting set status = #{status}, audit_user_id = #{auditUserId}, " +
            "audit_time = #{auditTime}, audit_remark = #{auditRemark} where id = #{id}")
    void updateAudit(Meeting meeting);


    /**
     * 删除（逻辑删除）
     */
    @Update("update meeting set delete_flag = 1 where id = #{id}")
    void deleteById(Long id);



    /**
     * 统计数量
     */
    long countByUserId(Long userId, String status);
}

