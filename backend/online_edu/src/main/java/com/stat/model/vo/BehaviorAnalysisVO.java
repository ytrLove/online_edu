package com.stat.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 行为分析VO类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorAnalysisVO implements Serializable {
    /**
     * 总行为记录数
     */
    private Integer totalBehaviors;
    
    /**
     * 总学习时长（分钟）
     */
    private Integer totalStudyDuration;
    
    /**
     * 平均每次学习时长（分钟）
     */
    private Double avgSessionDuration;
    
    /**
     * 平均完成率（0-1）
     */
    private Double avgCompletionRate;
    
    /**
     * 完成任务数
     */
    private Integer completedTasks;
    
    /**
     * 使用最多的行为类型
     */
    private String mostFrequentBehaviorType;
    
    /**
     * 行为类型分布
     */
    private Map<String, Integer> behaviorTypeDistribution;
    
    /**
     * 学习时间分布（小时为单位，0-23）
     */
    private Map<Integer, Integer> studyHourDistribution;
    
    /**
     * 活跃趋势数据
     */
    private List<ActivityData> activityTrend;
    
    /**
     * 学习进度数据
     */
    private List<ProgressData> progressTrend;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityData {
        /**
         * 日期
         */
        private String date;
        
        /**
         * 行为记录数
         */
        private Integer count;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressData {
        /**
         * 日期
         */
        private String date;
        
        /**
         * 完成率
         */
        private Double completionRate;
    }
} 