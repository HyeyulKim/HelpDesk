package com.helpdesk.statistics.controller;

import com.helpdesk.statistics.dto.DashboardDto;
import com.helpdesk.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public String dashboard(Model model) {
        DashboardDto dashboard = statisticsService.getDashboard();
        model.addAttribute("dashboard", dashboard);
        return "statistics/index";
    }
}