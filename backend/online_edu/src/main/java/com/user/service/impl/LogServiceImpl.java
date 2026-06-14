package com.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.mapper.OperationLogMapper;
import com.user.model.entity.OperationLog;
import com.user.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class LogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements LogService {

    @Autowired
    private OperationLogMapper operationLogMapper;


    @Override
    @Transactional
    public void recordOperLog(OperationLog operLog) {
        operationLogMapper.insert(operLog);
    }

    @Override
    public IPage<OperationLog> listModules(int page, int size, String username, Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        return operationLogMapper.selectModules(new Page<>(page, size), username, status, startTime, endTime);
    }

    @Override
    public void clearLogs() {
        log.info("开始清理日志");
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        wrapper.gt("created_at", LocalDateTime.now().minusDays(7));
        operationLogMapper.delete(wrapper);

    }


}
