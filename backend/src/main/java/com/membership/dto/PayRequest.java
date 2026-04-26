package com.membership.dto;

import java.math.BigDecimal;

public class PayRequest {
    private String memberId;
    private String orderType;
    private String relatedCardId;
    private BigDecimal amount;
    private BigDecimal giftedAmount;
    private String paymentMethod;

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
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
