package com.membership.dto;

import java.math.BigDecimal;

public class ConsumeResult {
    private BigDecimal giftUsed;
    private BigDecimal principalUsed;

    public ConsumeResult() {}

    public ConsumeResult(BigDecimal giftUsed, BigDecimal principalUsed) {
        this.giftUsed = giftUsed;
        this.principalUsed = principalUsed;
    }

    public BigDecimal getGiftUsed() { return giftUsed; }
    public void setGiftUsed(BigDecimal giftUsed) { this.giftUsed = giftUsed; }
    public BigDecimal getPrincipalUsed() { return principalUsed; }
    public void setPrincipalUsed(BigDecimal principalUsed) { this.principalUsed = principalUsed; }
}
