package com.stat.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 行为统计VO类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorStatsVO implements Serializable {
    /**
     * 总行为记录数
     */
    private Integer totalBehaviors;
    
    /**
     * 活跃学生数
     */
    private Integer activeStudents;
    
    /**
     * 平均学习时长（分钟）
     */
    private Double avgStudyDuration;
    
    /**
     * 平均完成率（0-1）
     */
    private Double avgCompletionRate;
    
    /**
     * 行为类型分布
     */
    private Map<String, Integer> behaviorTypeDistribution;
    
    /**
     * 每日活跃度数据
     */
    private List<DailyActivityData> activityTrend;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyActivityData {
        /**
         * 日期
         */
        private String date;
        
        /**
         * 活跃学生数
         */
        private Integer activeCount;
        
        /**
         * 行为记录数
         */
        private Integer behaviorCount;
    }
} 