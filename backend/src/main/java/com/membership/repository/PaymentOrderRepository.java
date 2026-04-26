package com.membership.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.membership.model.PaymentOrder;
import com.membership.model.PaymentStatus;

import org.springframework.stereotype.Repository;

@Repository
public class PaymentOrderRepository {
    private final Map<String, PaymentOrder> orders = new ConcurrentHashMap<>();

    public PaymentOrder save(PaymentOrder order) {
        orders.put(order.getId(), order);
        return order;
    }

    public Optional<PaymentOrder> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<PaymentOrder> findByMemberId(String memberId) {
        return orders.values().stream()
                .filter(o -> memberId.equals(o.getMemberId()))
                .collect(Collectors.toList());
    }

    public List<PaymentOrder> findByMemberIdAndStatus(String memberId, PaymentStatus status) {
        return orders.values().stream()
                .filter(o -> memberId.equals(o.getMemberId()) && status.equals(o.getStatus()))
                .collect(Collectors.toList());
    }

    public List<PaymentOrder> findAll() {
        return new ArrayList<>(orders.values());
    }

    public boolean existsById(String id) {
        return orders.containsKey(id);
    }

    public long count() {
        return orders.size();
    }
}
