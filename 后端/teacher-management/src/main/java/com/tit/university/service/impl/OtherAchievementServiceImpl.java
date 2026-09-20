package com.tit.university.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tit.university.mapper.OtherAchievementMapper;
import com.tit.university.pojo.OtherAchievement;
import com.tit.university.pojo.PageResult;
import com.tit.university.service.OtherAchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OtherAchievementServiceImpl implements OtherAchievementService {



    @Autowired
    private OtherAchievementMapper otherAchievementMapper;

    @Override
    public PageResult<OtherAchievement> page(Integer currentPage, Integer pageSize, Long userId, String status) {
        PageHelper.startPage(currentPage, pageSize);
        List<OtherAchievement> resultList = otherAchievementMapper.list(userId, status);
        Page<OtherAchievement> page = (Page<OtherAchievement>) resultList;
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public void add(OtherAchievement oa) {
        if (oa.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        oa.setStatus("待审核");
        otherAchievementMapper.insert(oa);

    }

    @Override
    public void audit(Long id, String status, Long auditUserId, String auditRemark) {
        OtherAchievement oa = otherAchievementMapper.selectById(id);
        if (oa == null) {
            return;

        }
        oa.setStatus(status);
        oa.setAuditUserId(auditUserId);
        oa.setAuditTime(LocalDateTime.now());
        oa.setAuditRemark(auditRemark);
        otherAchievementMapper.updateAudit(oa);
    }

    @Override
    public void delete(Long id) {

        otherAchievementMapper.deleteById(id);

    }
}

