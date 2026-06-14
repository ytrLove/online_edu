package com.home.service;

import com.home.domain.DashboardResponse;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {
    /**
     * 获取仪表盘基本信息
     * @return 仪表盘响应数据
     */
    DashboardResponse getInfo();
    
    /**
     * 获取用户最近活动
     * @return 用户最近活动列表
     */
    List<Map<String, Object>> getRecentActivities();
    
    /**
     * 获取用户学习进度统计
     * @return 学习进度数据
     */
    Map<String, Object> getLearningProgress();
    
    /**
     * 获取近期通知
     * @return 通知列表
     */
    List<Map<String, Object>> getNotifications();
    
    /**
     * 获取特定课程的仪表盘数据
     * @param courseId 课程ID
     * @return 课程仪表盘数据
     */
    Map<String, Object> getCourseDashboard(Long courseId);
}
