package com.tit.university.service;

import com.tit.university.pojo.Meeting;
import com.tit.university.pojo.PageResult;

public interface MeetingService {

    /**
     * 分页查询
     * @param currentPage
     * @param pageSize
     * @param userId
     * @param status
     * @return
     */
    PageResult<Meeting> page(Integer currentPage, Integer pageSize, Long userId, String status);

    /**
     * 添加会议
     * @param meeting
     */
    void add(Meeting meeting);


    /**
     * 审核
     * @param id
     * @param status
     * @param auditUserId
     * @param auditRemark
     */
    void audit(Long id, String status, Long auditUserId, String auditRemark);

    /**
     * 删除会议
     * @param id
     */
    void delete(Long id);
}

