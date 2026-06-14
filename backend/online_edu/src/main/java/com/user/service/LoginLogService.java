package com.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.domain.AjaxResult;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.user.mapper.LoginLogMapper;
import com.user.model.entity.LoginLog;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.io.File;
import java.net.InetAddress;
import java.time.LocalDateTime;

@Service
public class LoginLogService extends ServiceImpl<LoginLogMapper, LoginLog> {

    @Value("${geoip.database.path}")
    private String geoipDatabasePath;

    /**
     * 异步记录登录日志
     */
    @Async
    public void recordLoginLog(HttpServletRequest request, String username, boolean success, String message) {
        LoginLog log = new LoginLog();
        log.setUsername(username);
        log.setLoginTime(LocalDateTime.now());
        log.setIpAddress(getClientIp(request));
        log.setLocation(resolveIpLocation(log.getIpAddress()));
        parseUserAgent(request, log);
        log.setStatus(success ? 1 : 0);
        log.setMessage(message);

        this.save(log);
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0]; // 处理多层代理
    }

    /**
     * 解析IP地理位置
     */
    public  String resolveIpLocation(String ip) {
        try {
            if (geoipDatabasePath != null && !geoipDatabasePath.isEmpty()) {
                File database = new File(geoipDatabasePath);
                if (database.exists()) {
                    DatabaseReader reader = new DatabaseReader.Builder(database).build();
                    InetAddress ipAddress = InetAddress.getByName(ip);
                    CityResponse response = reader.city(ipAddress);

                    String country = response.getCountry().getNames().get("zh-CN");
                    String city = response.getCity().getNames().get("zh-CN");
                    return country + (city != null ? ", " + city : "");
                }
            }
            return "未知位置";
        } catch (Exception e) {
            return "解析失败";
        }
    }

    /**
     * 解析User-Agent信息
     */
    private void parseUserAgent(HttpServletRequest request, LoginLog log) {
        String userAgentStr = request.getHeader("User-Agent");
        if (userAgentStr != null && !userAgentStr.isEmpty()) {
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
            log.setBrowser(userAgent.getBrowser().getName());
            log.setOs(userAgent.getOperatingSystem().getName());
        } else {
            log.setBrowser("未知浏览器");
            log.setOs("未知操作系统");
        }
    }

    /**
     * 分页查询登录日志
     */
    public IPage<LoginLog> getLoginLogPage(int page, int size,
                                      String username, Integer status,
                                      LocalDateTime startTime, LocalDateTime endTime) {
        Page<LoginLog> pageParam = new Page<>(page, size);
        return baseMapper.selectLoginLogs(pageParam, username, status, startTime, endTime);
    }
}
