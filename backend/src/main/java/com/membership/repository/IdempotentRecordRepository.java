package com.membership.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.membership.model.IdempotentRecord;

import org.springframework.stereotype.Repository;

@Repository
public class IdempotentRecordRepository {
    private final Map<String, IdempotentRecord> records = new ConcurrentHashMap<>();

    public IdempotentRecord save(IdempotentRecord record) {
        records.put(record.getId(), record);
        return record;
    }

    public Optional<IdempotentRecord> findById(String id) {
        return Optional.ofNullable(records.get(id));
    }

    public Optional<IdempotentRecord> findByIdempotentKey(String idempotentKey) {
        return records.values().stream()
                .filter(r -> idempotentKey.equals(r.getIdempotentKey()))
                .findFirst();
    }

    public boolean existsByIdempotentKey(String idempotentKey) {
        return records.values().stream()
                .anyMatch(r -> idempotentKey.equals(r.getIdempotentKey()));
    }

    public List<IdempotentRecord> findAll() {
        return new ArrayList<>(records.values());
    }

    public void deleteOlderThan(LocalDateTime time) {
        List<String> toDelete = records.values().stream()
                .filter(r -> r.getCreatedAt().isBefore(time))
                .map(IdempotentRecord::getId)
                .collect(Collectors.toList());
        toDelete.forEach(records::remove);
    }
}
