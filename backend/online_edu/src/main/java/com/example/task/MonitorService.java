package com.example.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class MonitorService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 系统监控任务 - 每分钟执行一次
     * @param params JSON格式参数
     */
    public void checkSystemHealth(String params) {
        log.info("系统监控任务执行开始，参数: {}", params);
        try {
            // 解析参数
            Map<String, Object> paramMap = objectMapper.readValue(params, Map.class);
            Map<String, Number> thresholds = (Map<String, Number>) paramMap.get("thresholds");
            double cpuThreshold = thresholds.getOrDefault("cpu", 90).doubleValue();
            double memoryThreshold = thresholds.getOrDefault("memory", 85).doubleValue();

            // 获取当前系统指标
            double cpuUsage = getCpuUsage(); // 实现获取CPU使用率的方法
            double memoryUsage = getMemoryUsage(); // 实现获取内存使用率的方法

            log.info("当前系统状态 - CPU: {}%, 内存: {}%", cpuUsage, memoryUsage);

            // 检查是否超过阈值
            if (cpuUsage > cpuThreshold) {
                log.warn("CPU使用率超过阈值: {} > {}", cpuUsage, cpuThreshold);
                // 发送警报
                // alertService.sendAlert("CPU使用率过高", String.format("当前CPU使用率: %.2f%%", cpuUsage));
            }

            if (memoryUsage > memoryThreshold) {
                log.warn("内存使用率超过阈值: {} > {}", memoryUsage, memoryThreshold);
                // 发送警报
                // alertService.sendAlert("内存使用率过高", String.format("当前内存使用率: %.2f%%", memoryUsage));
            }

            log.info("系统监控检查完成");
        } catch (Exception e) {
            log.error("系统监控任务执行异常", e);
            throw new RuntimeException("系统监控任务执行失败", e);
        }
    }

    // 模拟获取CPU使用率
    private double getCpuUsage() {
        // 实际项目中可以从系统监控工具获取
        return Math.random() * 100;
    }

    // 模拟获取内存使用率
    private double getMemoryUsage() {
        // 实际项目中可以从系统监控工具获取
        return 30 + Math.random() * 60;
    }
}