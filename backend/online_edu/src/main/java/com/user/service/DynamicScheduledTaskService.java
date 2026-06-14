package com.user.service;

import com.user.mapper.ScheduledTaskLogMapper;
import com.user.mapper.ScheduledTaskMapper;
import com.user.model.entity.ScheduledTask;
import com.user.model.entity.ScheduledTaskLog;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.support.CronExpression;

import java.lang.reflect.Method;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicScheduledTaskService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final Map<Long, ScheduledFuture<?>> taskFutures = new ConcurrentHashMap<>();

    private final ScheduledTaskMapper taskMapper;
    private final ScheduledTaskLogMapper logMapper;
    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        List<ScheduledTask> enabledTasks = taskMapper.selectByStatus(1);
        enabledTasks.forEach(this::scheduleTask);
    }

    public void scheduleTask(ScheduledTask task) {
        cancelTask(task.getId());

        Runnable taskRunner = () -> {
            ScheduledTaskLog log = new ScheduledTaskLog();
            log.setTaskId(task.getId());
            log.setStartTime(new Date(System.currentTimeMillis()));
            log.setExecutionStatus(0); // 初始状态为失败

            try {
                Object bean = applicationContext.getBean(Class.forName(task.getTaskClass()));
                Method method = bean.getClass().getMethod(task.getTaskMethod(), String.class);
                Object result = method.invoke(bean, task.getParams());

                log.setExecutionStatus(1); // 执行成功
                log.setExecutionResult(result != null ? result.toString() : "执行成功");
            } catch (Exception e) {
                log.setErrorMessage(e.getMessage());
                log.setExecutionResult("执行失败: " + e.getMessage());
            } finally {
                log.setEndTime(new Date(System.currentTimeMillis()));
                logMapper.insert(log);
            }
        };

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                taskRunner,
                0,
                getDelayFromCron(task.getCronExpression()),
                TimeUnit.MILLISECONDS
        );

        taskFutures.put(task.getId(), future);
    }

    public void cancelTask(Long taskId) {
        ScheduledFuture<?> future = taskFutures.get(taskId);
        if (future != null) {
            future.cancel(true);
            taskFutures.remove(taskId);
        }
    }

    public void updateTask(ScheduledTask task) {
        if (task.getStatus() == 1) {
            scheduleTask(task);
        } else {
            cancelTask(task.getId());
        }
        taskMapper.updateById(task);
    }



    /**
     * 根据Cron表达式计算下次执行延迟时间(毫秒)
     * @param cronExpression Cron表达式
     * @return 距离下次执行的毫秒数
     * @throws IllegalArgumentException 如果Cron表达式无效
     */

//    private long getDelayFromCron(String cronExpression) {
//       return 1000 * 60;
//    }

    private long getDelayFromCron(String cronExpression) {
        try {
            // 使用Spring的CronExpression
            CronExpression expression = CronExpression.parse(cronExpression);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime next = expression.next(now);

            if (next == null) {
                throw new IllegalArgumentException("无法计算下次执行时间，可能是无效的Cron表达式: " + cronExpression);
            }

            log.info("下次执行时间: {}", next);

            return Duration.between(now, next).toMillis();
        } catch (IllegalArgumentException e) {
            log.error("无效的Cron表达式: {}", cronExpression, e);
            throw new IllegalArgumentException("无效的Cron表达式: " + cronExpression, e);
        }
    }


    @PreDestroy
    public void destroy() {
        scheduler.shutdownNow();
    }

    public void executeTaskNow(ScheduledTask task) {
        scheduleTask(task);
    }
}
