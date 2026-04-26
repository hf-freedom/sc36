package com.membership.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CreatePackageCardRequest {
    private String memberId;
    private String name;
    private String packageId;
    private BigDecimal price;
    private Integer totalTimes;
    private List<String> serviceIds;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getTotalTimes() { return totalTimes; }
    public void setTotalTimes(Integer totalTimes) { this.totalTimes = totalTimes; }
    public List<String> getServiceIds() { return serviceIds; }
    public void setServiceIds(List<String> serviceIds) { this.serviceIds = serviceIds; }
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }
}
