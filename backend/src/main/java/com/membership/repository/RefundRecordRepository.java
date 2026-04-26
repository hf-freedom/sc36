package com.membership.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.membership.model.RefundRecord;

import org.springframework.stereotype.Repository;

@Repository
public class RefundRecordRepository {
    private final Map<String, RefundRecord> records = new ConcurrentHashMap<>();

    public RefundRecord save(RefundRecord record) {
        records.put(record.getId(), record);
        return record;
    }

    public Optional<RefundRecord> findById(String id) {
        return Optional.ofNullable(records.get(id));
    }

    public Optional<RefundRecord> findByRelatedConsumptionId(String consumptionId) {
        return records.values().stream()
                .filter(r -> consumptionId.equals(r.getRelatedConsumptionId()))
                .findFirst();
    }

    public List<RefundRecord> findByMemberId(String memberId) {
        return records.values().stream()
                .filter(r -> memberId.equals(r.getMemberId()))
                .collect(Collectors.toList());
    }

    public List<RefundRecord> findByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return records.values().stream()
                .filter(r -> r.getRefundedAt() != null 
                        && !r.getRefundedAt().isBefore(start) 
                        && r.getRefundedAt().isBefore(end))
                .collect(Collectors.toList());
    }

    public List<RefundRecord> findAll() {
        return new ArrayList<>(records.values());
    }

    public boolean existsById(String id) {
        return records.containsKey(id);
    }

    public long count() {
        return records.size();
    }
}
