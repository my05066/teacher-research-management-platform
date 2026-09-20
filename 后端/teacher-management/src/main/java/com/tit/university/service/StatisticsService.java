package com.tit.university.service;

import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getDashboardStats(Long userId);
}

