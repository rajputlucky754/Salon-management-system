package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Stylist — a type of Staff specializing in hair and beauty.
 * Demonstrates polymorphism by overriding getStaffType().
 */
@Entity
@DiscriminatorValue("STYLIST")
public class Stylist extends Staff {

    private String certificationLevel; // Junior, Senior, Master

    public Stylist() {}

    @Override
    public String getStaffType() {
        return "Stylist";
    }

    public String getCertificationLevel() { return certificationLevel; }
    public void setCertificationLevel(String certificationLevel) { this.certificationLevel = certificationLevel; }
}
