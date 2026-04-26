package com.membership.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyReport {
    private String id;
    private LocalDate reportDate;
    private Integer totalMembers;
    private Integer activeCards;
    private BigDecimal totalBalance;
    private BigDecimal totalGiftBalance;
    private BigDecimal totalConsumption;
    private BigDecimal totalSettlement;
    private Integer newMembers;
    private Integer newCards;
    private Integer consumptionCount;
    private Integer refundCount;
    private LocalDateTime createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public Integer getTotalMembers() { return totalMembers; }
    public void setTotalMembers(Integer totalMembers) { this.totalMembers = totalMembers; }
    public Integer getActiveCards() { return activeCards; }
    public void setActiveCards(Integer activeCards) { this.activeCards = activeCards; }
    public BigDecimal getTotalBalance() { return totalBalance; }
    public void setTotalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; }
    public BigDecimal getTotalGiftBalance() { return totalGiftBalance; }
    public void setTotalGiftBalance(BigDecimal totalGiftBalance) { this.totalGiftBalance = totalGiftBalance; }
    public BigDecimal getTotalConsumption() { return totalConsumption; }
    public void setTotalConsumption(BigDecimal totalConsumption) { this.totalConsumption = totalConsumption; }
    public BigDecimal getTotalSettlement() { return totalSettlement; }
    public void setTotalSettlement(BigDecimal totalSettlement) { this.totalSettlement = totalSettlement; }
    public Integer getNewMembers() { return newMembers; }
    public void setNewMembers(Integer newMembers) { this.newMembers = newMembers; }
    public Integer getNewCards() { return newCards; }
    public void setNewCards(Integer newCards) { this.newCards = newCards; }
    public Integer getConsumptionCount() { return consumptionCount; }
    public void setConsumptionCount(Integer consumptionCount) { this.consumptionCount = consumptionCount; }
    public Integer getRefundCount() { return refundCount; }
    public void setRefundCount(Integer refundCount) { this.refundCount = refundCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
