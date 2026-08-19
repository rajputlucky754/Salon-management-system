package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.dto.DashboardSummaryDTO;
import com.cjcc.yakalabs.sakurasaki.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final DashboardService dashService;

    public DashboardController(DashboardService dashService) {
        this.dashService = dashService;
    }

    @GetMapping("/summary")
    public DashboardSummaryDTO getSummart(){
        return dashService.getSummary();
    }
}
