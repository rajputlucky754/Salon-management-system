package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class RedeemedReward {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Customer customer;
    
    private String rewardName;
    
    private Integer pointsCost;
    
    private LocalDateTime redeemedDate;
    
    private boolean isUsed;
    
    public RedeemedReward() {}
    
    public RedeemedReward(Customer customer, String rewardName, Integer pointsCost) {
        this.customer = customer;
        this.rewardName = rewardName;
        this.pointsCost = pointsCost;
        this.redeemedDate = LocalDateTime.now();
        this.isUsed = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getRewardName() { return rewardName; }
    public void setRewardName(String rewardName) { this.rewardName = rewardName; }
    public Integer getPointsCost() { return pointsCost; }
    public void setPointsCost(Integer pointsCost) { this.pointsCost = pointsCost; }
    public LocalDateTime getRedeemedDate() { return redeemedDate; }
    public void setRedeemedDate(LocalDateTime redeemedDate) { this.redeemedDate = redeemedDate; }
    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean isUsed) { this.isUsed = isUsed; }
}
