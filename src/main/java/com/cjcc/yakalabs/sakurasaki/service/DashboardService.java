package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.dto.DashboardSummaryDTO;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import com.cjcc.yakalabs.sakurasaki.repository.CustomerRepository;
import com.cjcc.yakalabs.sakurasaki.repository.SalonServiceRepository;
import com.cjcc.yakalabs.sakurasaki.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final AppointmentRepository appointmentRepo;
    private final CustomerRepository customerRepo;
    private final SalonServiceRepository salonServiceRepo;
    private final StaffRepository staffRepo;

    public DashboardService(AppointmentRepository appointmentRepo,
                            CustomerRepository customerRepo,
                            SalonServiceRepository salonServiceRepo,
                            StaffRepository staffRepo) {
        this.appointmentRepo = appointmentRepo;
        this.customerRepo = customerRepo;
        this.salonServiceRepo = salonServiceRepo;
        this.staffRepo = staffRepo;
    }

    public DashboardSummaryDTO getSummary() {
        long totalCustomers      = customerRepo.count();
        long totalServices       = salonServiceRepo.count();
        long totalStaff          = staffRepo.count();
        long todayAppointments   = appointmentRepo.countByAppointmentDate(LocalDate.now());
        double totalRevenue      = appointmentRepo.sumRevenueCompleted();

        return new DashboardSummaryDTO(totalCustomers, totalServices, totalStaff, todayAppointments, totalRevenue);
    }
}
