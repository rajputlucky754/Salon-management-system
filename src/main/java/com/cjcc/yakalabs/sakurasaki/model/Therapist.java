package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Therapist — a type of Staff specializing in therapeutic treatments.
 * Demonstrates polymorphism by overriding getStaffType().
 */
@Entity
@DiscriminatorValue("THERAPIST")
public class Therapist extends Staff {

    private String therapyType; // Swedish, Deep Tissue, Aromatherapy

    public Therapist() {}

    @Override
    public String getStaffType() {
        return "Therapist";
    }

    public String getTherapyType() { return therapyType; }
    public void setTherapyType(String therapyType) { this.therapyType = therapyType; }
}
