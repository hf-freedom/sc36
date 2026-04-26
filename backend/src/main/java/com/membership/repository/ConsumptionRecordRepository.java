package com.membership.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.membership.model.ConsumptionRecord;

import org.springframework.stereotype.Repository;

@Repository
public class ConsumptionRecordRepository {
    private final Map<String, ConsumptionRecord> records = new ConcurrentHashMap<>();

    public ConsumptionRecord save(ConsumptionRecord record) {
        records.put(record.getId(), record);
        return record;
    }

    public Optional<ConsumptionRecord> findById(String id) {
        return Optional.ofNullable(records.get(id));
    }

    public List<ConsumptionRecord> findByMemberId(String memberId) {
        return records.values().stream()
                .filter(r -> memberId.equals(r.getMemberId()))
                .collect(Collectors.toList());
    }

    public List<ConsumptionRecord> findByCardId(String cardId) {
        return records.values().stream()
                .filter(r -> cardId.equals(r.getCardId()))
                .collect(Collectors.toList());
    }

    public Optional<ConsumptionRecord> findByIdempotentKey(String idempotentKey) {
        return records.values().stream()
                .filter(r -> idempotentKey.equals(r.getIdempotentKey()))
                .findFirst();
    }

    public List<ConsumptionRecord> findByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return records.values().stream()
                .filter(r -> r.getConsumedAt() != null 
                        && !r.getConsumedAt().isBefore(start) 
                        && r.getConsumedAt().isBefore(end))
                .collect(Collectors.toList());
    }

    public List<ConsumptionRecord> findAll() {
        return new ArrayList<>(records.values());
    }

    public boolean existsById(String id) {
        return records.containsKey(id);
    }

    public long count() {
        return records.size();
    }
}
