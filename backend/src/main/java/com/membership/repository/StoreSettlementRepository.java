package com.membership.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.membership.model.SettlementStatus;
import com.membership.model.StoreSettlement;

import org.springframework.stereotype.Repository;

@Repository
public class StoreSettlementRepository {
    private final Map<String, StoreSettlement> settlements = new ConcurrentHashMap<>();

    public StoreSettlement save(StoreSettlement settlement) {
        settlements.put(settlement.getId(), settlement);
        return settlement;
    }

    public Optional<StoreSettlement> findById(String id) {
        return Optional.ofNullable(settlements.get(id));
    }

    public List<StoreSettlement> findByStoreId(String storeId) {
        return settlements.values().stream()
                .filter(s -> storeId.equals(s.getStoreId()))
                .collect(Collectors.toList());
    }

    public List<StoreSettlement> findByStoreIdAndStatus(String storeId, SettlementStatus status) {
        return settlements.values().stream()
                .filter(s -> storeId.equals(s.getStoreId()) && status.equals(s.getStatus()))
                .collect(Collectors.toList());
    }

    public Optional<StoreSettlement> findByConsumptionId(String consumptionId) {
        return settlements.values().stream()
                .filter(s -> consumptionId.equals(s.getConsumptionId()))
                .findFirst();
    }

    public List<StoreSettlement> findByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return settlements.values().stream()
                .filter(s -> s.getCreatedAt() != null 
                        && !s.getCreatedAt().isBefore(start) 
                        && s.getCreatedAt().isBefore(end))
                .collect(Collectors.toList());
    }

    public List<StoreSettlement> findAll() {
        return new ArrayList<>(settlements.values());
    }

    public boolean existsById(String id) {
        return settlements.containsKey(id);
    }

    public long count() {
        return settlements.size();
    }
}
