package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.StaffService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;

@Controller
@RequestMapping("/admin/staff")
public class StaffManagementController {

    private final StaffService staffService;

    public StaffManagementController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public String listStaff(@RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Authentication auth, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        model.addAttribute("username", auth.getName());
        if (search != null && !search.isBlank()) {
            model.addAttribute("staffList", staffService.searchByName(search, pageable));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("staffList", staffService.findAll(pageable));
        }
        return "admin/staff";
    }

    @PostMapping("/create")
    public String createStaff(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) String specialization,
                              @RequestParam(required = false) String staffType,
                              @RequestParam(required = false) String workingDays,
                              @RequestParam(required = false) String startTime,
                              @RequestParam(required = false) String endTime,
                              RedirectAttributes redirectAttributes) {
        try {
            LocalTime start = startTime != null && !startTime.isBlank() ? LocalTime.parse(startTime) : null;
            LocalTime end = endTime != null && !endTime.isBlank() ? LocalTime.parse(endTime) : null;
            
            if (start != null && end != null) {
                if (end.isAfter(LocalTime.of(20, 0))) {
                    throw new RuntimeException("Shop closes at 8 PM (20:00). Cannot register staff ending after 20:00.");
                }
                if (java.time.Duration.between(start, end).toHours() < 5) {
                    throw new RuntimeException("Staff must have at least a 5-hour shift.");
                }
            }
            staffService.createStaff(firstName, lastName, email, phone, specialization,
                    staffType, workingDays, start, end);
            redirectAttributes.addFlashAttribute("success", "Staff member registered!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("prevFirstName", firstName);
            redirectAttributes.addFlashAttribute("prevLastName", lastName);
            redirectAttributes.addFlashAttribute("prevEmail", email);
            redirectAttributes.addFlashAttribute("prevPhone", phone);
            redirectAttributes.addFlashAttribute("prevSpecialization", specialization);
            redirectAttributes.addFlashAttribute("prevStaffType", staffType);
            redirectAttributes.addFlashAttribute("prevWorkingDays", workingDays);
            redirectAttributes.addFlashAttribute("prevStartTime", startTime);
            redirectAttributes.addFlashAttribute("prevEndTime", endTime);
            redirectAttributes.addFlashAttribute("openModal", "createStaffModal");
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/{id}/update")
    public String updateStaff(@PathVariable Long id,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) String specialization,
                              @RequestParam(required = false) String workingDays,
                              @RequestParam(required = false) String startTime,
                              @RequestParam(required = false) String endTime,
                              RedirectAttributes redirectAttributes) {
        try {
            LocalTime start = startTime != null && !startTime.isBlank() ? LocalTime.parse(startTime) : null;
            LocalTime end = endTime != null && !endTime.isBlank() ? LocalTime.parse(endTime) : null;
            
            if (start != null && end != null) {
                if (end.isAfter(LocalTime.of(20, 0))) {
                    throw new RuntimeException("Shop closes at 8 PM (20:00). Cannot register staff ending after 20:00.");
                }
                if (java.time.Duration.between(start, end).toHours() < 5) {
                    throw new RuntimeException("Staff must have at least a 5-hour shift.");
                }
            }
            staffService.updateStaff(id, firstName, lastName, email, phone, specialization, workingDays, start, end);
            redirectAttributes.addFlashAttribute("success", "Staff member updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id) {
        staffService.toggleActive(id);
        return "redirect:/admin/staff";
    }

    @PostMapping("/{id}/delete")
    public String deleteStaff(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            staffService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Staff member deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete staff: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }
}
