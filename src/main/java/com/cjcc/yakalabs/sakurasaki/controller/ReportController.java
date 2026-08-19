package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.dto.ReportDTO;
import com.cjcc.yakalabs.sakurasaki.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Appointments per day
    @GetMapping("/appointments")
    public ReportDTO appointments(
            @RequestParam String from,
            @RequestParam String to) {
        return reportService.appointmentsPerDay(LocalDate.parse(from), LocalDate.parse(to));
    }

    // Most booked services
    @GetMapping("/services")
    public ReportDTO services(
            @RequestParam String from,
            @RequestParam String to) {
        return reportService.mostBookedServices(LocalDate.parse(from), LocalDate.parse(to));
    }
}
