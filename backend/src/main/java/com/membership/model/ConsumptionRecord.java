package com.membership.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConsumptionRecord {
    private String id;
    private String memberId;
    private String cardId;
    private String cardType;
    private String storeId;
    private String serviceId;
    private String serviceName;
    private BigDecimal amount;
    private BigDecimal principalUsed;
    private BigDecimal giftUsed;
    private Integer timesUsed;
    private String idempotentKey;
    private LocalDateTime consumedAt;
    private LocalDateTime createdAt;
    private boolean refunded;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }
    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getPrincipalUsed() { return principalUsed; }
    public void setPrincipalUsed(BigDecimal principalUsed) { this.principalUsed = principalUsed; }
    public BigDecimal getGiftUsed() { return giftUsed; }
    public void setGiftUsed(BigDecimal giftUsed) { this.giftUsed = giftUsed; }
    public Integer getTimesUsed() { return timesUsed; }
    public void setTimesUsed(Integer timesUsed) { this.timesUsed = timesUsed; }
    public String getIdempotentKey() { return idempotentKey; }
    public void setIdempotentKey(String idempotentKey) { this.idempotentKey = idempotentKey; }
    public LocalDateTime getConsumedAt() { return consumedAt; }
    public void setConsumedAt(LocalDateTime consumedAt) { this.consumedAt = consumedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isRefunded() { return refunded; }
    public void setRefunded(boolean refunded) { this.refunded = refunded; }
}
