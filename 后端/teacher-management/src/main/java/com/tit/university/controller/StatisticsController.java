package com.tit.university.controller;

import com.tit.university.pojo.Result;
import com.tit.university.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public Result getDashboardStats(@RequestParam(required = false) Long userId) {
        log.info("工作台统计 userId={}", userId);
        return Result.success(statisticsService.getDashboardStats(userId));
    }
}

