package com.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.core.domain.AjaxResult;

import com.user.mapper.ScheduledTaskLogMapper;
import com.user.mapper.ScheduledTaskMapper;
import com.user.model.entity.ScheduledTask;
import com.user.model.entity.ScheduledTaskLog;
import com.user.service.DynamicScheduledTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务管理控制器
 */
@RestController
@RequestMapping("/api/scheduled-tasks")
@RequiredArgsConstructor
public class ScheduledTaskController {
    private final DynamicScheduledTaskService taskService;
    private final ScheduledTaskMapper taskMapper;
    private final ScheduledTaskLogMapper logMapper;

    /**
     * 获取所有定时任务列表
     */
    @GetMapping
    public AjaxResult getAllTasks(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "taskName", required = false) String taskName,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        IPage<ScheduledTask> page = new Page<>(pageNum, pageSize);

        if (taskName != null || status != null) {
            LambdaQueryWrapper<ScheduledTask> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(ScheduledTask::getTaskName, taskName)
                    .eq(ScheduledTask::getStatus, status);
            return AjaxResult.success("获取定时任务列表成功", taskMapper.selectPage(page, queryWrapper));
        }else {
            return AjaxResult.success("获取定时任务列表成功", taskMapper.selectPage(page, null));
        }
        
    }

    /**
     * 分页查询定时任务
     */
    @GetMapping("/page")
    public AjaxResult<IPage<ScheduledTask>> getTasksByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // 这里需要实现分页查询逻辑
        // 可以使用MyBatis-Plus的分页插件
        // Page<ScheduledTask> page = new Page<>(pageNum, pageSize);
        // Page<ScheduledTask> result = taskMapper.selectPage(page, null);
        // return AjaxResult.success(PageResult.of(result));
        return AjaxResult.success("分页查询成功", null);
    }

    /**
     * 创建定时任务
     */
    @PostMapping
    public AjaxResult<ScheduledTask> createTask(@RequestBody ScheduledTask task) {
        taskMapper.insert(task);
        taskService.scheduleTask(task);
        return AjaxResult.success("定时任务创建成功", task);
    }

    /**
     * 更新定时任务
     */
    @PutMapping("/{id}")
    public AjaxResult<ScheduledTask> updateTask(@PathVariable Long id, @RequestBody ScheduledTask task) {
        task.setId(id);
        taskService.updateTask(task);
        taskMapper.updateById(task);
        return AjaxResult.success("定时任务更新成功", task);
    }

    /**
     * 删除定时任务
     */
    @DeleteMapping("/{id}")
    public AjaxResult<Void> deleteTask(@PathVariable Long id) {
        taskService.cancelTask(id);
        taskMapper.deleteById(id);
        return AjaxResult.success();
    }

    /**
     * 获取任务执行日志
     */
    @GetMapping("/{id}/logs")
    public AjaxResult<List<ScheduledTaskLog>> getTaskLogs(@PathVariable Long id) {
        List<ScheduledTaskLog> logs = logMapper.selectByTaskId(id);
        return AjaxResult.success("获取任务日志成功", logs);
    }

    /**
     * 立即执行任务
     */
    @PostMapping("/{id}/execute")
    public AjaxResult<Void> executeTaskNow(@PathVariable Long id) {
        ScheduledTask task = taskMapper.selectById(id);
        if (task == null) {
            return AjaxResult.fail("任务不存在");
        }
        taskService.executeTaskNow(task);
        return AjaxResult.success("任务已开始执行");
    }

    /**
     * 更改任务状态
     */
    @PutMapping("/{id}/status")
    public AjaxResult<Void> changeTaskStatus(@PathVariable Long id, @RequestParam Integer status) {
        ScheduledTask task = taskMapper.selectById(id);
        if (task == null) {
            return AjaxResult.fail("任务不存在");
        }
        task.setStatus(status);
        taskMapper.updateById(task);
        taskService.updateTask(task);
        return AjaxResult.success("任务状态更新成功");
    }

    /**
     * 批量删除任务
     */
    @DeleteMapping("/batch")
    public AjaxResult<Void> batchDeleteTasks(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return AjaxResult.fail("请选择要删除的任务");
        }
        ids.forEach(id -> {
            taskService.cancelTask(id);
            taskMapper.deleteById(id);
        });
        return AjaxResult.success("批量删除任务成功");
    }
}