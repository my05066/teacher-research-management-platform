package com.tit.university.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tit.university.mapper.PatentMapper;
import com.tit.university.pojo.Patent;
import com.tit.university.pojo.PageResult;
import com.tit.university.service.PatentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatentServiceImpl implements PatentService {




    @Autowired
    private PatentMapper patentMapper;

    @Override
    public PageResult<Patent> page(Integer currentPage, Integer pageSize, Long userId, String status) {
        PageHelper.startPage(currentPage, pageSize);
        List<Patent> rows = patentMapper.list(userId, status);
        Page<Patent> p = (Page<Patent>) rows;
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Override
    public void add(Patent patent) {
        if (patent.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        patent.setStatus("待审核");
        patentMapper.insert(patent);
    }

    @Override
    public void audit(Long id, String status, Long auditUserId, String auditRemark) {
        Patent pt = patentMapper.selectById(id);
        if (pt != null) {
            pt.setStatus(status);
            pt.setAuditUserId(auditUserId);
            pt.setAuditTime(LocalDateTime.now());
            pt.setAuditRemark(auditRemark);
            patentMapper.updateAudit(pt);
        }
    }

    @Override
    public void delete(Long id) {
        patentMapper.deleteById(id);
    }
}

