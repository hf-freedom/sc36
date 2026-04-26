package com.membership.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StoreSettlement {
    private String id;
    private String storeId;
    private String consumptionId;
    private String memberId;
    private BigDecimal totalAmount;
    private BigDecimal principalAmount;
    private BigDecimal giftAmount;
    private BigDecimal settlementRate;
    private BigDecimal settlementAmount;
    private LocalDateTime createdAt;
    private SettlementStatus status;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }
    public String getConsumptionId() { return consumptionId; }
    public void setConsumptionId(String consumptionId) { this.consumptionId = consumptionId; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }
    public BigDecimal getGiftAmount() { return giftAmount; }
    public void setGiftAmount(BigDecimal giftAmount) { this.giftAmount = giftAmount; }
    public BigDecimal getSettlementRate() { return settlementRate; }
    public void setSettlementRate(BigDecimal settlementRate) { this.settlementRate = settlementRate; }
    public BigDecimal getSettlementAmount() { return settlementAmount; }
    public void setSettlementAmount(BigDecimal settlementAmount) { this.settlementAmount = settlementAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public SettlementStatus getStatus() { return status; }
    public void setStatus(SettlementStatus status) { this.status = status; }
}
