package com.example.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class StatisticsService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 每小时统计任务 - 每小时执行一次
     * @param params JSON格式参数
     */
    public void generateHourlyReport(String params) {
        log.info("每小时统计任务执行开始，参数: {}", params);
        try {
            // 解析参数
            Map<String, Object> paramMap = objectMapper.readValue(params, Map.class);
            String reportType = (String) paramMap.getOrDefault("reportType", "summary");

            log.info("开始生成{}类型的每小时统计报告...", reportType);
            // 这里实现具体的统计逻辑
            // 例如: generateReport(reportType);

            log.info("每小时统计报告生成完成");
        } catch (Exception e) {
            log.error("每小时统计任务执行异常", e);
            throw new RuntimeException("每小时统计任务执行失败", e);
        }
    }
}