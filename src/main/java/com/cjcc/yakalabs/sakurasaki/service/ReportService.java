package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.dto.ReportDTO;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final AppointmentRepository appointmentRepo;

    public ReportService(AppointmentRepository appointmentRepo) {
        this.appointmentRepo = appointmentRepo;
    }

    // Appointments per day within a date range
    public ReportDTO appointmentsPerDay(LocalDate from, LocalDate to) {
        List<Object[]> rows = appointmentRepo.countAppointmentsPerDay(from, to);

        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String date = row[0].toString();
            Long count  = ((Number) row[1]).longValue();
            data.put(date, count);
        }

        return new ReportDTO(from, to, data);
    }

    // Most booked services within a date range
    public ReportDTO mostBookedServices(LocalDate from, LocalDate to) {
        List<Object[]> rows = appointmentRepo.countByServiceBetween(from, to);

        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String serviceName = (String) row[0];
            Long count         = ((Number) row[1]).longValue();
            data.put(serviceName, count);
        }

        return new ReportDTO(from, to, data);
    }
}
