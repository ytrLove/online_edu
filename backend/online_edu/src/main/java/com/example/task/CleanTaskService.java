package com.example.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class CleanTaskService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 每日数据清理任务 - 每天凌晨1点执行
     * @param params JSON格式参数
     */
    public void cleanExpiredData(String params) {
        log.info("每日清理任务执行开始，参数: {}", params);
        try {
            // 解析参数
            Map<String, Object> paramMap = objectMapper.readValue(params, Map.class);
            int daysToKeep = (int) paramMap.getOrDefault("daysToKeep", 30);

            log.info("开始清理超过{}天的过期数据...", daysToKeep);
            // 这里实现具体的数据清理逻辑
            // 例如: yourRepository.deleteByCreateTimeBefore(LocalDateTime.now().minusDays(daysToKeep));

            log.info("数据清理完成");
        } catch (Exception e) {
            log.error("每日清理任务执行异常", e);
            throw new RuntimeException("每日清理任务执行失败", e);
        }
    }
}
