package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import com.cjcc.yakalabs.sakurasaki.service.StaffService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST endpoint for checking staff availability.
 * Used by the booking form (AJAX) to only show free staff for a given date/time.
 */
@RestController
@RequestMapping("/api/staff")
public class StaffAvailabilityController {

    private final StaffService staffService;
    private final AppointmentService appointmentService;

    public StaffAvailabilityController(StaffService staffService, AppointmentService appointmentService) {
        this.staffService = staffService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/available")
    public List<Map<String, Object>> getAvailableStaff(
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam(defaultValue = "60") int duration) {

        LocalDate d = LocalDate.parse(date);
        LocalTime t = LocalTime.parse(time);

        return staffService.findActive().stream()
                .filter(s -> appointmentService.isStaffAvailable(s.getId(), d, t, duration, null))
                .map(s -> Map.<String, Object>of(
                        "id", s.getId(),
                        "name", s.getFirstName() + " " + s.getLastName(),
                        "type", s.getStaffType(),
                        "specialization", s.getSpecialization() != null ? s.getSpecialization() : ""
                ))
                .collect(Collectors.toList());
    }
}
