package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.repository.StaffRepository;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    private final UserRepository userRepo;
    private final StaffRepository staffRepo;
    private final AppointmentService appointmentService;

    public StaffDashboardController(UserRepository userRepo, StaffRepository staffRepo,
                                     AppointmentService appointmentService) {
        this.userRepo = userRepo;
        this.staffRepo = staffRepo;
        this.appointmentService = appointmentService;
    }

    private void populateStaffModel(Authentication auth, Model model) {
        var user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("username", auth.getName());

        if (user instanceof Staff staffMember) {
            model.addAttribute("staff", staffMember);
            var appointments = appointmentService.findByStaff(staffMember.getId());
            model.addAttribute("appointments", appointments);

            // Today's appointments
            List<Appointment> todayAppts = appointments.stream()
                    .filter(a -> a.getAppointmentDate().equals(LocalDate.now()))
                    .toList();
            model.addAttribute("todayAppointments", todayAppts);
            model.addAttribute("todayCount", todayAppts.size());

            // Calculate hours booked today
            double hoursBooked = todayAppts.stream()
                    .mapToDouble(a -> a.getService().getDurationMinutes() / 60.0)
                    .sum();
            model.addAttribute("hoursBooked", String.format("%.1f", hoursBooked));

            // Estimated Tips (15% of total service cost)
            double estTips = todayAppts.stream()
                    .mapToDouble(a -> a.getService().getPrice() * 0.15)
                    .sum();
            model.addAttribute("estTips", String.format("%.2f", estTips));
        } else {
            model.addAttribute("allStaffAppointments", appointmentService.findAll());
        }
    }

    @GetMapping("/dashboard")
    public String staffDashboard(Authentication auth, Model model) {
        populateStaffModel(auth, model);
        return "staff/dashboard";
    }

    @GetMapping("/calendar")
    public String calendar(Authentication auth, Model model) {
        populateStaffModel(auth, model);
        return "staff/calendar";
    }

    @GetMapping("/performance")
    public String performance(Authentication auth, Model model) {
        populateStaffModel(auth, model);
        return "staff/performance";
    }

    @GetMapping("/clients")
    public String clients(Authentication auth, Model model) {
        populateStaffModel(auth, model);
        return "staff/clients";
    }

    @GetMapping("/inventory")
    public String inventory(Authentication auth, Model model) {
        populateStaffModel(auth, model);
        return "staff/inventory";
    }

    @PostMapping("/appointments/{id}/complete")
    public String completeAppointment(@PathVariable Long id, Authentication auth,
                                       RedirectAttributes redirectAttributes) {
        try {
            appointmentService.changeStatus(id, "COMPLETED");
            redirectAttributes.addFlashAttribute("success", "Appointment marked as completed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/dashboard";
    }
}

