package com.tit.university.service;

import com.tit.university.pojo.Essay;
import com.tit.university.pojo.PageResult;

public interface EssayService {
    /**
     * 分页查询
     * @param currentPage
     * @param pageSize
     * @param userId
     * @param status
     * @return
     */
    PageResult<Essay> page(Integer currentPage, Integer pageSize, Long userId, String status);

    /**
     * 添加论文
     * @param essay
     */
    void add(Essay essay);


    /***
     *
     * 审核
     * @param id
     * @param status
     * @param auditUserId
     * @param auditRemark
     */
    void audit(Long id, String status, Long auditUserId, String auditRemark);

    /**
     * 删除论文
     * @param id
     */
    void delete(Long id);

}

