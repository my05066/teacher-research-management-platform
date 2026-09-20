package com.tit.university.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tit.university.mapper.StudentAwardMapper;
import com.tit.university.pojo.StudentAward;
import com.tit.university.pojo.PageResult;
import com.tit.university.service.StudentAwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentAwardServiceImpl implements StudentAwardService {


    @Autowired
    private StudentAwardMapper studentAwardMapper;



    @Override
    public PageResult<StudentAward> page(Integer currentPage, Integer pageSize, Long userId, String status) {
        PageHelper.startPage(currentPage, pageSize);
        Page<StudentAward> pg = (Page<StudentAward>) studentAwardMapper.list(userId, status);
        return new PageResult<>(pg.getTotal(), pg.getResult());
    }

    @Override
    public void add(StudentAward sa) {
        if (sa.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        sa.setStatus("待审核");
        studentAwardMapper.insert(sa);
    }

    @Override
    public void audit(Long id, String status, Long auditUserId, String auditRemark) {
        StudentAward sa = studentAwardMapper.selectById(id);
        if (sa != null) {
            sa.setStatus(status);
            sa.setAuditUserId(auditUserId);
            sa.setAuditTime(LocalDateTime.now());
            sa.setAuditRemark(auditRemark);
            studentAwardMapper.updateAudit(sa);
        }
    }

    @Override
    public void delete(Long id) {
        studentAwardMapper.deleteById(id);
    }
}

