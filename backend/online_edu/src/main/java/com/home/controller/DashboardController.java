package com.home.controller;


import com.common.core.domain.AjaxResult;
import com.home.domain.DashboardResponse;
import com.home.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/info")
    public AjaxResult<DashboardResponse> info() {
        DashboardResponse info = dashboardService.getInfo();

        return AjaxResult.success(info);
    }
}
