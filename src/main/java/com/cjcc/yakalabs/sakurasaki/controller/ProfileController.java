package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Admin;
import com.cjcc.yakalabs.sakurasaki.model.Customer;
import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import com.cjcc.yakalabs.sakurasaki.service.CustomerService;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final CustomerService customerService;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentService appointmentService;

    public ProfileController(CustomerService customerService, UserRepository userRepo, PasswordEncoder passwordEncoder, AppointmentService appointmentService) {
        this.customerService = customerService;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String viewProfile(Authentication auth) {
        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user instanceof Admin) {
            return "redirect:/admin/dashboard";
        }

        if (user instanceof Staff) {
            return "redirect:/staff/dashboard";
        }

        return "redirect:/customer/dashboard";
    }
}
