package com.tit.university.service;

import com.tit.university.pojo.Award;
import com.tit.university.pojo.PageResult;

public interface AwardService {
    /**
     * 分页查询
     * @param currentPage
     * @param pageSize
     * @param userId
     * @param status
     * @return
     */
    PageResult<Award> page(Integer currentPage, Integer pageSize, Long userId, String status);


    /**
     * 添加获奖
     * @param award
     */
    void add(Award award);


    /**
     * 审核
     * @param id
     * @param status
     * @param auditUserId
     * @param auditRemark
     */
    void audit(Long id, String status, Long auditUserId, String auditRemark);

    /**
     * 删除获奖
     * @param id
     */
    void delete(Long id);
}

