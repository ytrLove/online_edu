package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.model.entity.ScheduledTaskLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface ScheduledTaskLogMapper extends BaseMapper<ScheduledTaskLog> {
    @Select("SELECT * FROM sys_scheduled_task_log WHERE task_id = #{taskId} ORDER BY start_time DESC")
    List<ScheduledTaskLog> selectByTaskId(@Param("taskId") Long taskId);
}
