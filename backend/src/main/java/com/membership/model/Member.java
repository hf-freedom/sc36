package com.membership.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Member {
    private String id;
    private String name;
    private String phone;
    private MemberLevel level;
    private BigDecimal balance;
    private BigDecimal giftedBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> storedValueCardIds = new ArrayList<>();
    private List<String> packageCardIds = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public MemberLevel getLevel() { return level; }
    public void setLevel(MemberLevel level) { this.level = level; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public BigDecimal getGiftedBalance() { return giftedBalance; }
    public void setGiftedBalance(BigDecimal giftedBalance) { this.giftedBalance = giftedBalance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<String> getStoredValueCardIds() { return storedValueCardIds; }
    public void setStoredValueCardIds(List<String> storedValueCardIds) { this.storedValueCardIds = storedValueCardIds; }
    public List<String> getPackageCardIds() { return packageCardIds; }
    public void setPackageCardIds(List<String> packageCardIds) { this.packageCardIds = packageCardIds; }
}
