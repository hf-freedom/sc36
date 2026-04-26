package com.membership.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateStoredValueCardRequest {
    private String memberId;
    private String name;
    private BigDecimal rechargeAmount;
    private BigDecimal giftedAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getRechargeAmount() { return rechargeAmount; }
    public void setRechargeAmount(BigDecimal rechargeAmount) { this.rechargeAmount = rechargeAmount; }
    public BigDecimal getGiftedAmount() { return giftedAmount; }
    public void setGiftedAmount(BigDecimal giftedAmount) { this.giftedAmount = giftedAmount; }
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }
}
