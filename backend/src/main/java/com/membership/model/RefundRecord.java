package com.membership.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RefundRecord {
    private String id;
    private String memberId;
    private String cardId;
    private String cardType;
    private String relatedConsumptionId;
    private BigDecimal refundAmount;
    private BigDecimal principalRefunded;
    private BigDecimal giftRefunded;
    private Integer timesRestored;
    private String reason;
    private LocalDateTime refundedAt;
    private LocalDateTime createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }
    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public String getRelatedConsumptionId() { return relatedConsumptionId; }
    public void setRelatedConsumptionId(String relatedConsumptionId) { this.relatedConsumptionId = relatedConsumptionId; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public BigDecimal getPrincipalRefunded() { return principalRefunded; }
    public void setPrincipalRefunded(BigDecimal principalRefunded) { this.principalRefunded = principalRefunded; }
    public BigDecimal getGiftRefunded() { return giftRefunded; }
    public void setGiftRefunded(BigDecimal giftRefunded) { this.giftRefunded = giftRefunded; }
    public Integer getTimesRestored() { return timesRestored; }
    public void setTimesRestored(Integer timesRestored) { this.timesRestored = timesRestored; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getRefundedAt() { return refundedAt; }
    public void setRefundedAt(LocalDateTime refundedAt) { this.refundedAt = refundedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
