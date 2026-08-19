package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Customer;
import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.repository.CustomerRepository;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import com.cjcc.yakalabs.sakurasaki.service.SalonServiceService;
import com.cjcc.yakalabs.sakurasaki.service.ServicePackageService;
import com.cjcc.yakalabs.sakurasaki.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookingController {

    private final AppointmentService appointmentService;
    private final SalonServiceService salonServiceService;
    private final ServicePackageService packageService;
    private final StaffService staffService;
    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;

    public BookingController(AppointmentService appointmentService,
                             SalonServiceService salonServiceService,
                             ServicePackageService packageService,
                             StaffService staffService,
                             UserRepository userRepo,
                             CustomerRepository customerRepo) {
        this.appointmentService = appointmentService;
        this.salonServiceService = salonServiceService;
        this.packageService = packageService;
        this.staffService = staffService;
        this.userRepo = userRepo;
        this.customerRepo = customerRepo;
    }

    @GetMapping("/booking")
    public String bookingForm(@RequestParam(required = false) Long serviceId,
                              @RequestParam(required = false) String select,
                              @RequestParam(required = false) Long rescheduleId,
                              Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("services", salonServiceService.findActive());
        model.addAttribute("packages", packageService.findActive());
        model.addAttribute("staffList", staffService.findActive());
        Long selectedServiceId = resolveSelectedServiceId(serviceId, select);
        if (selectedServiceId != null) {
            model.addAttribute("selectedServiceId", selectedServiceId);
        }
        if (rescheduleId != null) {
            model.addAttribute("rescheduleId", rescheduleId);
        }
        return "booking/form";
    }

    private Long resolveSelectedServiceId(Long serviceId, String select) {
        if (select == null || select.isBlank()) {
            return serviceId;
        }
        try {
            if (select.startsWith("SVC_")) {
                return Long.parseLong(select.substring(4));
            }
            if (select.startsWith("PKG_")) {
                Long packageId = Long.parseLong(select.substring(4));
                return packageService.findById(packageId)
                        .flatMap(pkg -> pkg.getServices().stream().findFirst())
                        .map(com.cjcc.yakalabs.sakurasaki.model.SalonService::getId)
                        .orElse(serviceId);
            }
        } catch (NumberFormatException ignored) {
            return serviceId;
        }
        return serviceId;
    }

    /**
     * REST endpoint: returns available staff for a given date, time and service duration.
     */
    @GetMapping("/booking/available-staff")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAvailableStaff(
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam(required = false, defaultValue = "60") int durationMinutes,
            @RequestParam(required = false) Long excludeAppointmentId) {
        LocalDate d = LocalDate.parse(date);
        LocalTime t = LocalTime.parse(time);

        List<Staff> allStaff = staffService.findActive();
        List<Map<String, Object>> available = allStaff.stream()
                .filter(s -> appointmentService.isStaffAvailable(s.getId(), d, t, durationMinutes, excludeAppointmentId))
                .map(s -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", s.getId());
                    m.put("firstName", s.getFirstName());
                    m.put("lastName", s.getLastName());
                    m.put("specialization", s.getSpecialization());
                    m.put("staffType", s.getStaffType());
                    return m;
                })
                .toList();
        return ResponseEntity.ok(available);
    }

    @PostMapping("/booking")
    public String submitBooking(@RequestParam Long serviceId,
                                @RequestParam Long staffId,
                                @RequestParam String date,
                                @RequestParam String time,
                                @RequestParam(required = false) String notes,
                                @RequestParam(required = false) Long rescheduleId,
                                @RequestParam(required = false) String returnTo,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userRepo.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Customer customer;
            if (user instanceof Customer c) {
                customer = c;
            } else {
                customer = customerRepo.findByEmail(user.getEmail())
                        .orElseThrow(() -> new RuntimeException("Please complete your profile before booking."));
            }

            LocalDate appointmentDate = LocalDate.parse(date);
            LocalTime appointmentTime = LocalTime.parse(time);

            if (rescheduleId != null) {
                appointmentService.reschedule(rescheduleId, staffId, appointmentDate, appointmentTime, notes);
                redirectAttributes.addFlashAttribute("success", "Appointment rescheduled successfully!");
            } else {
                appointmentService.createAppointment(customer.getId(), serviceId, staffId,
                        appointmentDate, appointmentTime, notes);
                redirectAttributes.addFlashAttribute("success", "Appointment booked successfully!");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (returnTo != null && !returnTo.isBlank()) {
                return "redirect:" + returnTo;
            }
            return "redirect:/booking";
        }
        return "redirect:/customer/bookings";
    }

    @GetMapping("/my-appointments")
    public String myAppointments(Authentication auth, Model model) {
        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer;
        if (user instanceof Customer c) {
            customer = c;
        } else {
            customer = customerRepo.findByEmail(user.getEmail()).orElse(null);
        }

        model.addAttribute("username", auth.getName());
        if (customer != null) {
            model.addAttribute("appointments", appointmentService.findByCustomer(customer.getId()));
        }
        model.addAttribute("customer", customer);
        return "booking/my-appointments";
    }

    @PostMapping("/my-appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, @RequestParam(required = false) String reason, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id, reason);
            redirectAttributes.addFlashAttribute("success", "Appointment cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/bookings";
    }
}
