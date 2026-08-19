package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;
import java.time.LocalTime;

/**
 * Staff extends User — adds salon-specific fields.
 * Stylist and Therapist further extend Staff.
 */
@Entity
@DiscriminatorValue("STAFF")
public class Staff extends User {

    private String specialization;
    private String workingDays; // Comma-separated: "MON,TUE,WED,THU,FRI"
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean active = true;

    public Staff() {}

    public Staff(String firstName, String lastName, String email, String phone, String specialization) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setPhone(phone);
        this.specialization = specialization;
        this.setRole("ROLE_STAFF");
        this.setEnabled(true);
    }

    /**
     * Polymorphic method — subclasses (Stylist, Therapist) override this.
     */
    public String getStaffType() {
        return "General";
    }

    // --- Getters and Setters ---

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getWorkingDays() { return workingDays; }
    public void setWorkingDays(String workingDays) { this.workingDays = workingDays; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
