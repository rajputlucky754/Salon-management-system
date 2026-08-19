package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/appointments")
public class AppointmentViewController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public String viewAllAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.findAll());
        model.addAttribute("pageTitle", "All Appointments");
        return "appointments-list";
    }

    @GetMapping("/completed")
    public String viewCompletedAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.findByStatus("COMPLETED"));
        model.addAttribute("pageTitle", "Completed Appointments");
        return "appointments-list";
    }

    @GetMapping("/{appointmentId}")
    public String viewAppointmentById(@PathVariable Long appointmentId, Model model) {
        Appointment appointment = appointmentService.findById(appointmentId).orElse(null);
        model.addAttribute("appointment", appointment);
        return "appointment-details";
    }
}
