package com.tit.university.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tit.university.mapper.EssayMapper;
import com.tit.university.pojo.Essay;
import com.tit.university.pojo.PageResult;
import com.tit.university.service.EssayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EssayServiceImpl implements EssayService {


    @Autowired
    private EssayMapper essayMapper;

    @Override
    public PageResult<Essay> page(Integer currentPage, Integer pageSize, Long userId, String status) {
        PageHelper.startPage(currentPage, pageSize);
        List<Essay> dataList = essayMapper.list(userId, status);
        Page<Essay> page = (Page<Essay>) dataList;
        return new PageResult<>(page.getTotal(), page.getResult());
    }


    @Override
    public void add(Essay essay) {
        if (essay.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        essay.setStatus("待审核");
        essayMapper.insert(essay);
    }


    @Override
    public void audit(Long id, String status, Long auditUserId, String auditRemark) {
        Essay e = essayMapper.selectById(id);
        if (e == null) {
            return;
        }
        e.setStatus(status);
        e.setAuditUserId(auditUserId);
        e.setAuditTime(LocalDateTime.now());
        e.setAuditRemark(auditRemark);
        essayMapper.updateAudit(e);
    }


    @Override
    public void delete(Long id) {

        essayMapper.deleteById(id);
    }
}

