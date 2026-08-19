package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * ServicePackage groups multiple SalonServices together with a discount.
 * Demonstrates composition (has-a list of services) and abstraction
 * (computed methods for total price and discounted price).
 */
@Entity
@Table(name = "service_packages")
public class ServicePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private double discountPercent; // e.g., 10.0 for 10%

    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "package_services",
        joinColumns = @JoinColumn(name = "package_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<SalonService> services = new HashSet<>();

    public ServicePackage() {}

    public ServicePackage(String name, String description, double discountPercent) {
        this.name = name;
        this.description = description;
        this.discountPercent = discountPercent;
    }

    // --- Computed methods (Abstraction) ---

    /**
     * Calculate total price by summing all service prices.
     */
    public double getTotalPrice() {
        return services.stream().mapToDouble(SalonService::getPrice).sum();
    }

    /**
     * Calculate discounted price.
     */
    public double getDiscountedPrice() {
        return getTotalPrice() * (1 - discountPercent / 100.0);
    }

    /**
     * Calculate total duration by summing all service durations.
     */
    public int getTotalDuration() {
        return services.stream().mapToInt(SalonService::getDurationMinutes).sum();
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Set<SalonService> getServices() { return services; }
    public void setServices(Set<SalonService> services) { this.services = services; }
}
