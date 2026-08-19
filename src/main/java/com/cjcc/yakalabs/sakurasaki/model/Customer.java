package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Customer extends User — inherits username, password, email, firstName, lastName, phone.
 * Demonstrates inheritance: Customer IS-A User.
 */
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    @Column(name = "membership_tier")
    private String membershipTier = "Bronze";

    public Customer() {}

    public Customer(String firstName, String lastName, String email, String phone) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setPhone(phone);
        this.setRole("ROLE_USER");
        this.setEnabled(true);
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }
}
