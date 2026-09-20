package com.tit.university.service;

import com.tit.university.pojo.StudentAward;
import com.tit.university.pojo.PageResult;

public interface StudentAwardService {
    /**
     * 分页查询
     * @param currentPage
     * @param pageSize
     * @param userId
     * @param status
     * @return
     */
    PageResult<StudentAward> page(Integer currentPage, Integer pageSize, Long userId, String status);

    /**
     * 添加学生获奖
     * @param studentAward
     */
    void add(StudentAward studentAward);

    /**
     * 审核学生获奖
     * @param id
     * @param status
     * @param auditUserId
     * @param auditRemark
     */
    void audit(Long id, String status, Long auditUserId, String auditRemark);


    /**
     * 删除学生获奖
     * @param id
     */
    void delete(Long id);


}

