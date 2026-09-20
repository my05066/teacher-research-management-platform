package com.tit.university.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tit.university.mapper.MeetingMapper;
import com.tit.university.pojo.Meeting;
import com.tit.university.pojo.PageResult;
import com.tit.university.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private MeetingMapper meetingMapper;

    @Override
    public PageResult<Meeting> page(Integer currentPage, Integer pageSize, Long userId, String status) {
        PageHelper.startPage(currentPage, pageSize);
        List<Meeting> items = meetingMapper.list(userId, status);
        Page<Meeting> pg = (Page<Meeting>) items;
        return new PageResult<>(pg.getTotal(), pg.getResult());
    }

    @Override
    public void add(Meeting meeting) {
        if (meeting.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        meeting.setStatus("待审核");
        meetingMapper.insert(meeting);
    }

    @Override
    public void audit(Long id, String status, Long auditUserId, String auditRemark) {
        Meeting m = meetingMapper.selectById(id);
        if (m == null) return;
        m.setStatus(status);
        m.setAuditUserId(auditUserId);
        m.setAuditTime(LocalDateTime.now());
        m.setAuditRemark(auditRemark);
        meetingMapper.updateAudit(m);
    }


    @Override
    public void delete(Long id) {
        meetingMapper.deleteById(id);
    }
}

