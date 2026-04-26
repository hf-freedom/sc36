package com.membership.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import com.membership.model.ConsumptionRecord;
import com.membership.model.SettlementStatus;
import com.membership.model.StoreSettlement;
import com.membership.repository.StoreSettlementRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreSettlementService {

    private static final BigDecimal DEFAULT_SETTLEMENT_RATE = new BigDecimal("0.90");

    @Autowired
    private StoreSettlementRepository storeSettlementRepository;

    public StoreSettlement createSettlement(ConsumptionRecord consumption) {
        StoreSettlement settlement = new StoreSettlement();
        settlement.setId(UUID.randomUUID().toString());
        settlement.setStoreId(consumption.getStoreId());
        settlement.setConsumptionId(consumption.getId());
        settlement.setMemberId(consumption.getMemberId());
        settlement.setTotalAmount(consumption.getAmount());
        settlement.setPrincipalAmount(consumption.getPrincipalUsed());
        settlement.setGiftAmount(consumption.getGiftUsed());
        
        BigDecimal settlementRate = getSettlementRate(consumption.getStoreId());
        settlement.setSettlementRate(settlementRate);
        
        BigDecimal settlementAmount = consumption.getPrincipalUsed()
                .multiply(settlementRate)
                .setScale(2, RoundingMode.HALF_UP);
        settlement.setSettlementAmount(settlementAmount);
        
        settlement.setStatus(SettlementStatus.PENDING);
        settlement.setCreatedAt(LocalDateTime.now());

        return storeSettlementRepository.save(settlement);
    }

    public StoreSettlement settle(String settlementId) {
        StoreSettlement settlement = storeSettlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("分账记录不存在"));

        if (settlement.getStatus() != SettlementStatus.PENDING) {
            throw new RuntimeException("分账状态异常，无法结算");
        }

        settlement.setStatus(SettlementStatus.SETTLED);
        return storeSettlementRepository.save(settlement);
    }

    private BigDecimal getSettlementRate(String storeId) {
        return DEFAULT_SETTLEMENT_RATE;
    }
}
