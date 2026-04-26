package com.membership.dto;

import java.math.BigDecimal;

public class RefundResult {
    private BigDecimal refundAmount;
    private BigDecimal deductedPrincipal;
    private BigDecimal deductedGift;
    private String message;

    public RefundResult() {}

    public RefundResult(BigDecimal refundAmount, BigDecimal deductedPrincipal, BigDecimal deductedGift, String message) {
        this.refundAmount = refundAmount;
        this.deductedPrincipal = deductedPrincipal;
        this.deductedGift = deductedGift;
        this.message = message;
    }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public BigDecimal getDeductedPrincipal() { return deductedPrincipal; }
    public void setDeductedPrincipal(BigDecimal deductedPrincipal) { this.deductedPrincipal = deductedPrincipal; }
    public BigDecimal getDeductedGift() { return deductedGift; }
    public void setDeductedGift(BigDecimal deductedGift) { this.deductedGift = deductedGift; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
