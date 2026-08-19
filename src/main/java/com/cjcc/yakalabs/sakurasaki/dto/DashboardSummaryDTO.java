package com.cjcc.yakalabs.sakurasaki.dto;

public class DashboardSummaryDTO {

    private long totalCustomers;
    private long totalServices;
    private long totalStaff;
    private long todayAppointments;
    private double totalRevenue;

    public DashboardSummaryDTO(long totalCustomers, long totalServices, long totalStaff, long todayAppointments) {
        this.totalCustomers = totalCustomers;
        this.totalServices = totalServices;
        this.totalStaff = totalStaff;
        this.todayAppointments = todayAppointments;
        this.totalRevenue = 0;
    }

    public DashboardSummaryDTO(long totalCustomers, long totalServices, long totalStaff, long todayAppointments, double totalRevenue) {
        this.totalCustomers = totalCustomers;
        this.totalServices = totalServices;
        this.totalStaff = totalStaff;
        this.todayAppointments = todayAppointments;
        this.totalRevenue = totalRevenue;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(long totalServices) {
        this.totalServices = totalServices;
    }

    public long getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(long totalStaff) {
        this.totalStaff = totalStaff;
    }

    public long getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(long todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}

