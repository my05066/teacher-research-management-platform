package com.tit.university.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tit.university.mapper.AwardMapper;
import com.tit.university.pojo.Award;
import com.tit.university.pojo.PageResult;
import com.tit.university.service.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AwardServiceImpl implements AwardService {

    @Autowired
    private AwardMapper awardMapper;

    @Override
    public PageResult<Award> page(Integer currentPage, Integer pageSize, Long userId, String status) {
        PageHelper.startPage(currentPage, pageSize);
        Page<Award> pg = (Page<Award>) awardMapper.list(userId, status);
        return new PageResult<>(pg.getTotal(), pg.getResult());
    }

    @Override
    public void add(Award award) {
        if (award.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        award.setStatus("待审核");
        awardMapper.insert(award);
    }

    @Override
    public void audit(Long id, String status, Long auditUserId, String auditRemark) {
        Award record = awardMapper.selectById(id);
        if (record == null) return;
        record.setStatus(status);
        record.setAuditUserId(auditUserId);
        record.setAuditTime(LocalDateTime.now());
        record.setAuditRemark(auditRemark);
        awardMapper.updateAudit(record);
    }

    @Override
    public void delete(Long id) {
        awardMapper.deleteById(id);
    }
}

