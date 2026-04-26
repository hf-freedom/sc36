package com.membership.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.membership.dto.RefundResult;
import com.membership.model.CardStatus;
import com.membership.model.Member;
import com.membership.model.PackageCard;
import com.membership.repository.MemberRepository;
import com.membership.repository.PackageCardRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageCardService {

    @Autowired
    private PackageCardRepository packageCardRepository;

    @Autowired
    private MemberRepository memberRepository;

    public PackageCard createCard(
            String memberId,
            String name,
            String packageId,
            BigDecimal price,
            Integer totalTimes,
            List<String> serviceIds,
            LocalDateTime validFrom,
            LocalDateTime validTo
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        PackageCard card = new PackageCard();
        card.setId(UUID.randomUUID().toString());
        card.setMemberId(memberId);
        card.setName(name);
        card.setPackageId(packageId);
        card.setPrice(price);
        card.setTotalTimes(totalTimes);
        card.setRemainingTimes(totalTimes);
        card.setServiceIds(serviceIds);
        card.setValidFrom(validFrom != null ? validFrom : LocalDateTime.now());
        card.setValidTo(validTo);
        card.setStatus(CardStatus.ACTIVE);
        card.setCreatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());

        PackageCard savedCard = packageCardRepository.save(card);

        member.getPackageCardIds().add(savedCard.getId());
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        return savedCard;
    }

    public Optional<PackageCard> getCardById(String id) {
        return packageCardRepository.findById(id);
    }

    public List<PackageCard> getCardsByMemberId(String memberId) {
        return packageCardRepository.findByMemberId(memberId);
    }

    public List<PackageCard> getActiveCardsByMemberId(String memberId) {
        return packageCardRepository.findByMemberIdAndStatus(memberId, CardStatus.ACTIVE);
    }

    public List<PackageCard> getCardsByMemberAndService(String memberId, String serviceId) {
        return packageCardRepository.findByMemberIdAndServiceId(memberId, serviceId);
    }

    public int consumeTimes(String cardId, int times) {
        PackageCard card = packageCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("套餐卡不存在"));

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("套餐卡状态异常，无法消费");
        }

        if (card.getValidTo() != null && LocalDateTime.now().isAfter(card.getValidTo())) {
            throw new RuntimeException("套餐卡已过期");
        }

        if (card.getRemainingTimes() < times) {
            throw new RuntimeException("套餐卡次数不足");
        }

        card.setRemainingTimes(card.getRemainingTimes() - times);
        
        if (card.getRemainingTimes() == 0) {
            card.setStatus(CardStatus.USED);
        }
        
        card.setUpdatedAt(LocalDateTime.now());
        packageCardRepository.save(card);

        return times;
    }

    public void restoreTimes(String cardId, int times) {
        PackageCard card = packageCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("套餐卡不存在"));

        card.setRemainingTimes(card.getRemainingTimes() + times);
        
        if (card.getStatus() == CardStatus.USED && card.getRemainingTimes() > 0) {
            card.setStatus(CardStatus.ACTIVE);
        }
        
        card.setUpdatedAt(LocalDateTime.now());
        packageCardRepository.save(card);
    }

    public void markAsExpired(String cardId) {
        PackageCard card = packageCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("套餐卡不存在"));

        card.setStatus(CardStatus.EXPIRED);
        card.setUpdatedAt(LocalDateTime.now());
        packageCardRepository.save(card);
    }

    public List<PackageCard> getExpiredCards() {
        return packageCardRepository.findExpiredCards(LocalDateTime.now());
    }

    public RefundResult refundCard(String cardId) {
        PackageCard card = packageCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("套餐卡不存在"));

        if (card.getStatus() != CardStatus.ACTIVE && card.getStatus() != CardStatus.EXPIRED) {
            throw new RuntimeException("套餐卡状态异常，无法退款");
        }

        BigDecimal totalPrice = card.getPrice();
        int totalTimes = card.getTotalTimes();
        int remainingTimes = card.getRemainingTimes();
        int usedTimes = totalTimes - remainingTimes;

        BigDecimal refundAmount = BigDecimal.ZERO;
        BigDecimal deductedAmount = BigDecimal.ZERO;

        StringBuilder message = new StringBuilder();
        message.append("套餐总价: ").append(totalPrice).append(" 元");
        message.append(", 总次数: ").append(totalTimes);
        message.append(", 已使用: ").append(usedTimes);
        message.append(", 剩余: ").append(remainingTimes);

        if (totalTimes > 0 && remainingTimes > 0) {
            BigDecimal pricePerTime = totalPrice.divide(new BigDecimal(totalTimes), 2, RoundingMode.HALF_UP);
            deductedAmount = pricePerTime.multiply(new BigDecimal(usedTimes));
            refundAmount = pricePerTime.multiply(new BigDecimal(remainingTimes));
            
            message.append(", 单次价格: ").append(pricePerTime).append(" 元");
            message.append(", 已消费金额: ").append(deductedAmount).append(" 元");
            message.append(", 可退金额: ").append(refundAmount).append(" 元");
        } else if (remainingTimes == 0) {
            deductedAmount = totalPrice;
            message.append(", 次数已全部使用，无可退金额");
        }

        card.setStatus(CardStatus.REFUNDED);
        card.setUpdatedAt(LocalDateTime.now());
        packageCardRepository.save(card);

        return new RefundResult(refundAmount, deductedAmount, BigDecimal.ZERO, message.toString());
    }
}
