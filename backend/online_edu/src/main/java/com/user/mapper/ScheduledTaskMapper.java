package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.model.entity.ScheduledTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface ScheduledTaskMapper extends BaseMapper<ScheduledTask> {
    @Select("SELECT * FROM sys_scheduled_task WHERE status = #{status}")
    List<ScheduledTask> selectByStatus(@Param("status") Integer status);
}