package com.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.common.core.domain.AjaxResult;
import com.user.model.entity.LoginLog;
import com.user.model.entity.OperationLog;
import com.user.service.LogService;
import com.user.service.LoginLogService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api")
public class LogController {

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private LogService logService;

    /**
     * 分页查询登录日志
     */

    @PreAuthorize("hasAuthority('system:loginlog:list')")
    @GetMapping("/login-logs")
    public AjaxResult<IPage<LoginLog>> getLoginLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        IPage<LoginLog> loginLogPage = loginLogService.getLoginLogPage(page, size, username, status, startTime, endTime);

        return AjaxResult.success(loginLogPage);
    }


    /**
     * 获取操作模块列表
     */

    @PreAuthorize("hasAuthority('system:loginlog:list')")
    @GetMapping("/modules-logs")
    public AjaxResult<IPage<OperationLog>> getModules(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false) String username,
                                                      @RequestParam(required = false) Integer status,
                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
       IPage<OperationLog> modules = logService.listModules(page, size, username, status, startTime, endTime);
        return AjaxResult.success(modules);
    }

    /**
     * 获取操作详细日志
     */

    @PreAuthorize("hasAuthority('system:loginlog:list')")
    @GetMapping("/operation-logs/{id}")
    public AjaxResult<OperationLog> getOperationLogs(@PathVariable("id") Long id) {
        log.info("获取操作详细日志" + id);
        OperationLog operationLog = logService.getById(id);
        return AjaxResult.success(operationLog);
    }



}
