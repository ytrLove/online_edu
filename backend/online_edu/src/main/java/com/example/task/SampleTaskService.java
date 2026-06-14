package com.example.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SampleTaskService {

    /**
     * 示例任务1 - 每5分钟执行一次
     * @param params JSON格式参数
     */
    public void sampleTask(String params) {
        log.info("示例任务1执行开始，参数: {}", params);
        try {
            // 这里实现具体的任务逻辑
            log.info("示例任务1正在处理业务...");
            // 模拟业务处理耗时
            Thread.sleep(1000);
            log.info("示例任务1执行完成");
        } catch (Exception e) {
            log.error("示例任务1执行异常", e);
            throw new RuntimeException("示例任务1执行失败", e);
        }
    }
}
