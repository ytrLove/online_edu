package com.example.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 每周工作报告 - 每周一上午9点执行
     * @param params JSON格式参数
     */
    public void generateWeeklyReport(String params) {
        log.info("每周报告任务执行开始，参数: {}", params);
        try {
            // 解析参数
            Map<String, Object> paramMap = objectMapper.readValue(params, Map.class);
            List<String> recipients = (List<String>) paramMap.get("recipients");

            log.info("开始生成每周工作报告，收件人: {}", recipients);
            // 这里实现具体的报告生成和发送逻辑
            // 例如:
            // String reportContent = generateReportContent();
            // emailService.send(recipients, "每周工作报告", reportContent);

            log.info("每周工作报告已发送");
        } catch (Exception e) {
            log.error("每周报告任务执行异常", e);
            throw new RuntimeException("每周报告任务执行失败", e);
        }
    }
}