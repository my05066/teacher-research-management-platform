package com.tit.university.service;

import com.tit.university.pojo.PageResult;
import com.tit.university.pojo.Program;
import com.tit.university.pojo.ProgramQueryParam;

public interface ProgramService {


    /**
     * 分页查询项目
     */
    PageResult<Program> page(ProgramQueryParam programQueryParam, Long userId, String status);


    /**
     * 添加项目
     */
    void add(Program program);


    /**
     * 审核项目
     */
    void audit(Long id, String status, Long auditUserId, String auditRemark);


    /**
     * 提交结题材料（横向项目）
     */
    void submitCompletionDoc(Long id, String completionDocPath, String completionDocName, Long completionDocSize);

    /**
     *
     *
     * 删除项目
     */
    void delete(Long id);
}
