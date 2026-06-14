package com.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.user.model.entity.OperationLog;

import java.time.LocalDateTime;

public interface LogService extends IService<OperationLog> {
     void recordOperLog(OperationLog operLog);



     IPage<OperationLog> listModules(int page, int size, String username, Integer status, LocalDateTime startTime, LocalDateTime endTime);


     void clearLogs();
}
