package com.membership.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.membership.model.CardStatus;
import com.membership.model.StoredValueCard;

import org.springframework.stereotype.Repository;

@Repository
public class StoredValueCardRepository {
    private final Map<String, StoredValueCard> cards = new ConcurrentHashMap<>();

    public StoredValueCard save(StoredValueCard card) {
        cards.put(card.getId(), card);
        return card;
    }

    public Optional<StoredValueCard> findById(String id) {
        return Optional.ofNullable(cards.get(id));
    }

    public List<StoredValueCard> findByMemberId(String memberId) {
        return cards.values().stream()
                .filter(c -> memberId.equals(c.getMemberId()))
                .collect(Collectors.toList());
    }

    public List<StoredValueCard> findByMemberIdAndStatus(String memberId, CardStatus status) {
        return cards.values().stream()
                .filter(c -> memberId.equals(c.getMemberId()) && status.equals(c.getStatus()))
                .collect(Collectors.toList());
    }

    public List<StoredValueCard> findExpiredCards(LocalDateTime now) {
        return cards.values().stream()
                .filter(c -> c.getStatus() == CardStatus.ACTIVE && 
                        c.getValidTo() != null && c.getValidTo().isBefore(now))
                .collect(Collectors.toList());
    }

    public List<StoredValueCard> findAll() {
        return new ArrayList<>(cards.values());
    }

    public boolean existsById(String id) {
        return cards.containsKey(id);
    }

    public long count() {
        return cards.size();
    }
}
