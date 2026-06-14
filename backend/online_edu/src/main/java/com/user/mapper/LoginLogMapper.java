package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.user.model.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;


public interface LoginLogMapper extends BaseMapper<LoginLog> {

    IPage<LoginLog> selectLoginLogs(Page<LoginLog> page,
                                    @Param("username") String username,
                                    @Param("status") Integer status,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);
}
