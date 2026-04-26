package com.membership.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.membership.model.DailyReport;

import org.springframework.stereotype.Repository;

@Repository
public class DailyReportRepository {
    private final Map<String, DailyReport> reports = new ConcurrentHashMap<>();

    public DailyReport save(DailyReport report) {
        reports.put(report.getId(), report);
        return report;
    }

    public Optional<DailyReport> findById(String id) {
        return Optional.ofNullable(reports.get(id));
    }

    public Optional<DailyReport> findByReportDate(LocalDate reportDate) {
        return reports.values().stream()
                .filter(r -> reportDate.equals(r.getReportDate()))
                .findFirst();
    }

    public List<DailyReport> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return reports.values().stream()
                .filter(r -> (r.getReportDate().isEqual(startDate) || r.getReportDate().isAfter(startDate))
                        && (r.getReportDate().isEqual(endDate) || r.getReportDate().isBefore(endDate)))
                .collect(Collectors.toList());
    }

    public List<DailyReport> findAll() {
        return new ArrayList<>(reports.values());
    }

    public boolean existsByReportDate(LocalDate reportDate) {
        return reports.values().stream()
                .anyMatch(r -> reportDate.equals(r.getReportDate()));
    }

    public long count() {
        return reports.size();
    }
}
