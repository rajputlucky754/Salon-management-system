package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Admin extends User — adds hierarchical admin level.
 * adminLevel controls permission scope: CEO > MANAGER > ADMIN.
 */
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    private String adminLevel = "ADMIN"; // ADMIN, MANAGER, CEO

    public Admin() {}

    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }

    /**
     * Check if this admin has higher or equal level than the given level.
     */
    public boolean hasLevelOrHigher(String level) {
        int myRank = levelRank(this.adminLevel);
        int targetRank = levelRank(level);
        return myRank >= targetRank;
    }

    private int levelRank(String level) {
        return switch (level != null ? level : "ADMIN") {
            case "MANAGER" -> 3;
            case "CEO" -> 2;
            default -> 1; // ADMIN
        };
    }
}
