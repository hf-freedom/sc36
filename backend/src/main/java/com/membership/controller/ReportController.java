package com.membership.controller;

import java.time.LocalDate;
import java.util.List;

import com.membership.dto.ApiResponse;
import com.membership.model.DailyReport;
import com.membership.service.DailyReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private DailyReportService dailyReportService;

    @PostMapping("/daily/{date}")
    public ApiResponse<DailyReport> generateDailyReport(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            DailyReport report = dailyReportService.generateReportForDate(date);
            return ApiResponse.success("日报表生成成功", report);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/daily/{date}")
    public ApiResponse<DailyReport> getDailyReport(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return dailyReportService.getReportByDate(date)
                .map(report -> ApiResponse.success(report))
                .orElse(ApiResponse.error("日报表不存在"));
    }

    @GetMapping("/range")
    public ApiResponse<List<DailyReport>> getReportsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<DailyReport> reports = dailyReportService.getReportsByDateRange(startDate, endDate);
        return ApiResponse.success(reports);
    }
}
