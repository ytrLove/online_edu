package com.example.task;

import com.user.service.LogService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestTaskService {

    @Autowired
    private LogService logService;

    // 打印hello, world
    public void TestDome(String params) {
        System.out.println("hello, world");
    }

    public void cleanExpiredData(String params) {
        log.info("数据清理任务执行开始，参数: {}", params);
        logService.clearLogs();
    }

}
