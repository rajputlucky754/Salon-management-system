package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.*;
import com.cjcc.yakalabs.sakurasaki.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;
    private final DashboardService dashboardService;
    private final AppointmentService appointmentService;

    public AdminController(UserService userService,
                           AdminService adminService,
                           DashboardService dashboardService,
                           AppointmentService appointmentService) {
        this.userService = userService;
        this.adminService = adminService;
        this.dashboardService = dashboardService;
        this.appointmentService = appointmentService;
    }

    // ======================================================================
    //  DASHBOARD
    // ======================================================================

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("summary", dashboardService.getSummary());
        model.addAttribute("appointments", appointmentService.getLatestAppointments());
        return "admin/dashboard";
    }

    // ======================================================================
    //  REPORTS
    // ======================================================================

    @GetMapping("/reports")
    public String reports(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("summary", dashboardService.getSummary());
        return "admin/reports";
    }

    // ======================================================================
    //  USER MANAGEMENT
    // ======================================================================

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String search, 
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Authentication auth, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users;
        if (search != null && !search.isBlank()) {
            users = adminService.searchUsers(search, pageable);
        } else {
            users = userService.findAll(pageable);
        }
        model.addAttribute("username", auth.getName());
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        return "admin/users";
    }

    @PostMapping("/users/{id}/make-admin")
    public String makeAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.makeAdmin(id);
            redirectAttributes.addFlashAttribute("success", "User promoted to Admin.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-enabled")
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.toggleEnabled(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ======================================================================
    //  ADMIN MANAGEMENT
    // ======================================================================

    @GetMapping("/manage")
    public String manageAdmins(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Authentication auth, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        model.addAttribute("username", auth.getName());
        model.addAttribute("admins", adminService.listAdmins(pageable));
        return "admin/manage";
    }

    @PostMapping("/manage/create")
    public String createAdmin(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String email,
                              @RequestParam String adminLevel,
                              RedirectAttributes redirectAttributes) {
        try {
            adminService.createAdmin(username, password, email, adminLevel);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create admin: " + e.getMessage());
            redirectAttributes.addFlashAttribute("prevUsername", username);
            redirectAttributes.addFlashAttribute("prevEmail", email);
            redirectAttributes.addFlashAttribute("prevAdminLevel", adminLevel);
            redirectAttributes.addFlashAttribute("openModal", "createAdminModal");
        }
        return "redirect:/admin/manage";
    }

    @PostMapping("/manage/{id}/update")
    public String updateAdmin(@PathVariable Long id,
                              @RequestParam String username,
                              @RequestParam String email) {
        adminService.updateAdmin(id, username, email);
        return "redirect:/admin/manage";
    }

    @PostMapping("/manage/{id}/deactivate")
    public String deactivateAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deactivateAdmin(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/manage";
    }

    @PostMapping("/manage/{id}/demote")
    public String demoteAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.changeRole(id, "ROLE_USER");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/manage";
    }

    // ======================================================================
    //  CUSTOMER MANAGEMENT (Member 1 — Customer & Authentication Module)
    // ======================================================================

    @GetMapping("/customers")
    public String customers(@RequestParam(required = false) String search,
                            Authentication auth, Model model) {
        List<User> customers;
        if (search != null && !search.isBlank()) {
            customers = userService.searchCustomers(search);
        } else {
            customers = userService.findAllCustomers();
        }
        model.addAttribute("username", auth.getName());
        model.addAttribute("customers", customers);
        model.addAttribute("search", search);
        return "admin/customer-list";
    }

    @PostMapping("/customers/{id}/deactivate")
    public String deactivateCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deactivateUser(id);
            redirectAttributes.addFlashAttribute("success", "Customer deactivated successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/{id}/activate")
    public String activateCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.activateUser(id);
            redirectAttributes.addFlashAttribute("success", "Customer activated successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/{id}/delete")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Customer deleted permanently.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/customers";
    }
}