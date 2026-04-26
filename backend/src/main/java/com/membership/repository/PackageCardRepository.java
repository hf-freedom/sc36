package com.membership.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.membership.model.CardStatus;
import com.membership.model.PackageCard;

import org.springframework.stereotype.Repository;

@Repository
public class PackageCardRepository {
    private final Map<String, PackageCard> cards = new ConcurrentHashMap<>();

    public PackageCard save(PackageCard card) {
        cards.put(card.getId(), card);
        return card;
    }

    public Optional<PackageCard> findById(String id) {
        return Optional.ofNullable(cards.get(id));
    }

    public List<PackageCard> findByMemberId(String memberId) {
        return cards.values().stream()
                .filter(c -> memberId.equals(c.getMemberId()))
                .collect(Collectors.toList());
    }

    public List<PackageCard> findByMemberIdAndStatus(String memberId, CardStatus status) {
        return cards.values().stream()
                .filter(c -> memberId.equals(c.getMemberId()) && status.equals(c.getStatus()))
                .collect(Collectors.toList());
    }

    public List<PackageCard> findByMemberIdAndServiceId(String memberId, String serviceId) {
        return cards.values().stream()
                .filter(c -> memberId.equals(c.getMemberId()) 
                        && c.getStatus() == CardStatus.ACTIVE
                        && c.getServiceIds() != null 
                        && c.getServiceIds().contains(serviceId))
                .collect(Collectors.toList());
    }

    public List<PackageCard> findExpiredCards(LocalDateTime now) {
        return cards.values().stream()
                .filter(c -> c.getStatus() == CardStatus.ACTIVE && 
                        c.getValidTo() != null && c.getValidTo().isBefore(now))
                .collect(Collectors.toList());
    }

    public List<PackageCard> findAll() {
        return new ArrayList<>(cards.values());
    }

    public boolean existsById(String id) {
        return cards.containsKey(id);
    }

    public long count() {
        return cards.size();
    }
}
