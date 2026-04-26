package com.membership.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StoredValueCard {
    private String id;
    private String memberId;
    private String name;
    private BigDecimal rechargeAmount;
    private BigDecimal giftedAmount;
    private BigDecimal remainingPrincipal;
    private BigDecimal remainingGift;
    private BigDecimal frozenGift;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private CardStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getRechargeAmount() { return rechargeAmount; }
    public void setRechargeAmount(BigDecimal rechargeAmount) { this.rechargeAmount = rechargeAmount; }
    public BigDecimal getGiftedAmount() { return giftedAmount; }
    public void setGiftedAmount(BigDecimal giftedAmount) { this.giftedAmount = giftedAmount; }
    public BigDecimal getRemainingPrincipal() { return remainingPrincipal; }
    public void setRemainingPrincipal(BigDecimal remainingPrincipal) { this.remainingPrincipal = remainingPrincipal; }
    public BigDecimal getRemainingGift() { return remainingGift; }
    public void setRemainingGift(BigDecimal remainingGift) { this.remainingGift = remainingGift; }
    public BigDecimal getFrozenGift() { return frozenGift; }
    public void setFrozenGift(BigDecimal frozenGift) { this.frozenGift = frozenGift; }
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }
    public CardStatus getStatus() { return status; }
    public void setStatus(CardStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
