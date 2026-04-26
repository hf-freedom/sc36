package com.membership.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.membership.dto.ConsumeResult;
import com.membership.dto.RefundResult;
import com.membership.model.CardStatus;
import com.membership.model.Member;
import com.membership.model.StoredValueCard;
import com.membership.repository.MemberRepository;
import com.membership.repository.StoredValueCardRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoredValueCardService {

    @Autowired
    private StoredValueCardRepository storedValueCardRepository;

    @Autowired
    private MemberRepository memberRepository;

    public StoredValueCard createCard(
            String memberId,
            String name,
            BigDecimal rechargeAmount,
            BigDecimal giftedAmount,
            LocalDateTime validFrom,
            LocalDateTime validTo
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        StoredValueCard card = new StoredValueCard();
        card.setId(UUID.randomUUID().toString());
        card.setMemberId(memberId);
        card.setName(name);
        card.setRechargeAmount(rechargeAmount);
        card.setGiftedAmount(giftedAmount);
        card.setRemainingPrincipal(rechargeAmount);
        card.setRemainingGift(giftedAmount);
        card.setFrozenGift(BigDecimal.ZERO);
        card.setValidFrom(validFrom != null ? validFrom : LocalDateTime.now());
        card.setValidTo(validTo);
        card.setStatus(CardStatus.ACTIVE);
        card.setCreatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());

        StoredValueCard savedCard = storedValueCardRepository.save(card);
        
        member.getStoredValueCardIds().add(savedCard.getId());
        member.setBalance(member.getBalance().add(rechargeAmount));
        member.setGiftedBalance(member.getGiftedBalance().add(giftedAmount));
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        return savedCard;
    }

    public Optional<StoredValueCard> getCardById(String id) {
        return storedValueCardRepository.findById(id);
    }

    public List<StoredValueCard> getCardsByMemberId(String memberId) {
        return storedValueCardRepository.findByMemberId(memberId);
    }

    public List<StoredValueCard> getActiveCardsByMemberId(String memberId) {
        return storedValueCardRepository.findByMemberIdAndStatus(memberId, CardStatus.ACTIVE);
    }

    public void addBalance(String cardId, BigDecimal principal, BigDecimal gift) {
        StoredValueCard card = storedValueCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("储值卡不存在"));

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("储值卡状态异常，无法充值");
        }

        card.setRemainingPrincipal(card.getRemainingPrincipal().add(principal));
        card.setRemainingGift(card.getRemainingGift().add(gift));
        card.setUpdatedAt(LocalDateTime.now());
        storedValueCardRepository.save(card);
    }

    public ConsumeResult consumeBalance(String cardId, BigDecimal amount) {
        StoredValueCard card = storedValueCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("储值卡不存在"));

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("储值卡状态异常，无法消费");
        }

        if (card.getValidTo() != null && LocalDateTime.now().isAfter(card.getValidTo())) {
            throw new RuntimeException("储值卡已过期");
        }

        BigDecimal remaining = amount;
        BigDecimal giftUsed = BigDecimal.ZERO;
        BigDecimal principalUsed = BigDecimal.ZERO;

        if (card.getRemainingGift().compareTo(BigDecimal.ZERO) > 0) {
            giftUsed = card.getRemainingGift().min(remaining);
            card.setRemainingGift(card.getRemainingGift().subtract(giftUsed));
            remaining = remaining.subtract(giftUsed);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            if (card.getRemainingPrincipal().compareTo(remaining) < 0) {
                throw new RuntimeException("储值卡余额不足");
            }
            principalUsed = remaining;
            card.setRemainingPrincipal(card.getRemainingPrincipal().subtract(principalUsed));
        }

        card.setUpdatedAt(LocalDateTime.now());
        storedValueCardRepository.save(card);

        return new ConsumeResult(giftUsed, principalUsed);
    }

    public void refundBalance(String cardId, BigDecimal principal, BigDecimal gift) {
        StoredValueCard card = storedValueCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("储值卡不存在"));

        card.setRemainingPrincipal(card.getRemainingPrincipal().add(principal));
        card.setRemainingGift(card.getRemainingGift().add(gift));
        card.setUpdatedAt(LocalDateTime.now());
        storedValueCardRepository.save(card);
    }

    public void freezeExpiredGift(String cardId) {
        StoredValueCard card = storedValueCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("储值卡不存在"));

        if (card.getValidTo() != null && LocalDateTime.now().isAfter(card.getValidTo())) {
            BigDecimal giftToFreeze = card.getRemainingGift();
            card.setFrozenGift(card.getFrozenGift().add(giftToFreeze));
            card.setRemainingGift(BigDecimal.ZERO);
            card.setStatus(CardStatus.EXPIRED);
            card.setUpdatedAt(LocalDateTime.now());
            storedValueCardRepository.save(card);
        }
    }

    public List<StoredValueCard> getExpiredCards() {
        return storedValueCardRepository.findExpiredCards(LocalDateTime.now());
    }

    public RefundResult refundCard(String cardId, boolean allowPrincipalRefund) {
        StoredValueCard card = storedValueCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("储值卡不存在"));

        if (card.getStatus() != CardStatus.ACTIVE && card.getStatus() != CardStatus.EXPIRED) {
            throw new RuntimeException("储值卡状态异常，无法退款");
        }

        Member member = memberRepository.findById(card.getMemberId())
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        BigDecimal totalRecharge = card.getRechargeAmount();
        BigDecimal totalGift = card.getGiftedAmount();
        BigDecimal remainingPrincipal = card.getRemainingPrincipal();
        BigDecimal remainingGift = card.getRemainingGift();

        BigDecimal consumedPrincipal = totalRecharge.subtract(remainingPrincipal);
        BigDecimal consumedGift = totalGift.subtract(remainingGift);

        BigDecimal refundAmount = BigDecimal.ZERO;
        BigDecimal deductedPrincipal = consumedPrincipal;
        BigDecimal deductedGift = totalGift;

        StringBuilder message = new StringBuilder();
        message.append("总充值: ").append(totalRecharge).append(" 元");
        message.append(", 总赠送: ").append(totalGift).append(" 元");
        message.append(", 已消费本金: ").append(consumedPrincipal).append(" 元");
        message.append(", 已消费赠送: ").append(totalGift.subtract(remainingGift)).append(" 元");

        if (allowPrincipalRefund) {
            refundAmount = remainingPrincipal;
            message.append(", 可退本金: ").append(refundAmount).append(" 元");
        } else {
            deductedPrincipal = deductedPrincipal.add(remainingPrincipal);
            message.append(", 本金不可退，扣除剩余本金: ").append(remainingPrincipal).append(" 元");
        }

        message.append(", 赠送金额全部扣除");

        member.setBalance(member.getBalance().subtract(remainingPrincipal));
        member.setGiftedBalance(member.getGiftedBalance().subtract(remainingGift));
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        card.setStatus(CardStatus.REFUNDED);
        card.setRemainingPrincipal(BigDecimal.ZERO);
        card.setRemainingGift(BigDecimal.ZERO);
        card.setUpdatedAt(LocalDateTime.now());
        storedValueCardRepository.save(card);

        return new RefundResult(refundAmount, deductedPrincipal, deductedGift, message.toString());
    }
}
