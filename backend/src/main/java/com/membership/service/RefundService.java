package com.membership.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.membership.model.ConsumptionRecord;
import com.membership.model.RefundRecord;
import com.membership.model.SettlementStatus;
import com.membership.model.StoreSettlement;
import com.membership.repository.ConsumptionRecordRepository;
import com.membership.repository.RefundRecordRepository;
import com.membership.repository.StoreSettlementRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundService {

    @Autowired
    private RefundRecordRepository refundRecordRepository;

    @Autowired
    private ConsumptionRecordRepository consumptionRecordRepository;

    @Autowired
    private StoredValueCardService storedValueCardService;

    @Autowired
    private PackageCardService packageCardService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private StoreSettlementRepository storeSettlementRepository;

    public RefundRecord refundConsumption(String consumptionId, String reason) {
        ConsumptionRecord consumption = consumptionRecordRepository.findById(consumptionId)
                .orElseThrow(() -> new RuntimeException("消费记录不存在"));

        if (consumption.isRefunded()) {
            throw new RuntimeException("该消费已退款");
        }

        Optional<RefundRecord> existingRefund = refundRecordRepository.findByRelatedConsumptionId(consumptionId);
        if (existingRefund.isPresent()) {
            throw new RuntimeException("该消费已存在退款记录");
        }

        if ("STORED_VALUE".equals(consumption.getCardType())) {
            storedValueCardService.refundBalance(
                    consumption.getCardId(),
                    consumption.getPrincipalUsed(),
                    consumption.getGiftUsed()
            );
            memberService.updateBalance(
                    consumption.getMemberId(),
                    consumption.getPrincipalUsed(),
                    consumption.getGiftUsed()
            );
        } else if ("PACKAGE".equals(consumption.getCardType())) {
            packageCardService.restoreTimes(
                    consumption.getCardId(),
                    consumption.getTimesUsed()
            );
        }

        Optional<StoreSettlement> settlement = storeSettlementRepository.findByConsumptionId(consumptionId);
        if (settlement.isPresent() && settlement.get().getStatus() == SettlementStatus.PENDING) {
            settlement.get().setStatus(SettlementStatus.CANCELLED);
            storeSettlementRepository.save(settlement.get());
        }

        RefundRecord refund = new RefundRecord();
        refund.setId(UUID.randomUUID().toString());
        refund.setMemberId(consumption.getMemberId());
        refund.setCardId(consumption.getCardId());
        refund.setCardType(consumption.getCardType());
        refund.setRelatedConsumptionId(consumptionId);
        refund.setRefundAmount(consumption.getAmount());
        refund.setPrincipalRefunded(consumption.getPrincipalUsed());
        refund.setGiftRefunded(consumption.getGiftUsed());
        refund.setTimesRestored(consumption.getTimesUsed());
        refund.setReason(reason);
        refund.setRefundedAt(LocalDateTime.now());
        refund.setCreatedAt(LocalDateTime.now());

        RefundRecord savedRefund = refundRecordRepository.save(refund);

        consumption.setRefunded(true);
        consumptionRecordRepository.save(consumption);

        return savedRefund;
    }

    public Optional<RefundRecord> getRefundById(String id) {
        return refundRecordRepository.findById(id);
    }

    public Optional<RefundRecord> getRefundByConsumptionId(String consumptionId) {
        return refundRecordRepository.findByRelatedConsumptionId(consumptionId);
    }
}
