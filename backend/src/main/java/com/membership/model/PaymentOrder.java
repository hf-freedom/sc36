package com.membership.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentOrder {
    private String id;
    private String memberId;
    private String orderType;
    private String relatedCardId;
    private BigDecimal amount;
    private BigDecimal giftedAmount;
    private PaymentStatus status;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getRelatedCardId() { return relatedCardId; }
    public void setRelatedCardId(String relatedCardId) { this.relatedCardId = relatedCardId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getGiftedAmount() { return giftedAmount; }
    public void setGiftedAmount(BigDecimal giftedAmount) { this.giftedAmount = giftedAmount; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
