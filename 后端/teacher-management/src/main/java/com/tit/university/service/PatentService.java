package com.tit.university.service;

import com.tit.university.pojo.Patent;
import com.tit.university.pojo.PageResult;

public interface PatentService {
    /**
     * 分页查询
     * @param currentPage
     * @param pageSize
     * @param userId
     * @param status
     * @return
     */
    PageResult<Patent> page(Integer currentPage, Integer pageSize, Long userId, String status);

    /**
     * 添加专利
     * @param patent
     */
    void add(Patent patent);


    /**
     * 审核
     * @param id
     * @param status
     * @param auditUserId
     * @param auditRemark
     */
    void audit(Long id, String status, Long auditUserId, String auditRemark);


    /**
     * 删除专利
     * @param id
     */
    void delete(Long id);
}

