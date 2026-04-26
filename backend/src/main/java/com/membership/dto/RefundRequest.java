package com.membership.dto;

public class RefundRequest {
    private String consumptionId;
    private String reason;

    public String getConsumptionId() { return consumptionId; }
    public void setConsumptionId(String consumptionId) { this.consumptionId = consumptionId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
