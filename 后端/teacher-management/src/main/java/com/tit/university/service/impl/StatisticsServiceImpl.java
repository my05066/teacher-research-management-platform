package com.tit.university.service.impl;

import com.tit.university.mapper.*;
import com.tit.university.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private ProgramMapper programMapper;
    @Autowired
    private EssayMapper essayMapper;
    @Autowired
    private PatentMapper patentMapper;
    @Autowired
    private AwardMapper awardMapper;
    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private StudentAwardMapper studentAwardMapper;
    @Autowired
    private OtherAchievementMapper otherAchievementMapper;

    @Override
    public Map<String, Object> getDashboardStats(Long userId) {
        Map<String, Object> result = new HashMap<>();

        long programs = programMapper.countByUserId(userId, null);
        long essays = essayMapper.countByUserId(userId, null);
        long patents = patentMapper.countByUserId(userId, null);
        long awards = awardMapper.countByUserId(userId, null);
        long meetings = meetingMapper.countByUserId(userId, null);
        long stuAwards = studentAwardMapper.countByUserId(userId, null);
        long others = otherAchievementMapper.countByUserId(userId, null);

        // 待审核汇总
        long pending = programMapper.countByUserId(userId, "待审核")
                + essayMapper.countByUserId(userId, "待审核")
                + patentMapper.countByUserId(userId, "待审核")
                + awardMapper.countByUserId(userId, "待审核")
                + meetingMapper.countByUserId(userId, "待审核")
                + studentAwardMapper.countByUserId(userId, "待审核")
                + otherAchievementMapper.countByUserId(userId, "待审核");

        long ongoing = programMapper.countOngoingProjects(userId);
        long total = programs + essays + patents + awards + meetings + stuAwards + others;

        result.put("totalAchievements", total);
        result.put("pendingReview", pending);
        result.put("ongoingProjects", ongoing);
        result.put("annualPoints", 0);
        return result;

    }
}




