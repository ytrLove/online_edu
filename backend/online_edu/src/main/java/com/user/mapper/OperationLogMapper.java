package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.user.model.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    IPage<OperationLog> selectModules(Page<Object> objectPage, String username, Integer status, LocalDateTime startTime, LocalDateTime endTime);
}
