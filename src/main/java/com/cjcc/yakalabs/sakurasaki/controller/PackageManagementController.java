package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.SalonServiceService;
import com.cjcc.yakalabs.sakurasaki.service.ServicePackageService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/packages")
public class PackageManagementController {

    private final ServicePackageService packageService;
    private final SalonServiceService salonServiceService;

    public PackageManagementController(ServicePackageService packageService, SalonServiceService salonServiceService) {
        this.packageService = packageService;
        this.salonServiceService = salonServiceService;
    }

    @GetMapping
    public String listPackages(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("packages", packageService.findAll());
        model.addAttribute("allServices", salonServiceService.findActive());
        return "admin/packages";
    }

    @PostMapping("/create")
    public String createPackage(@RequestParam String name,
                                @RequestParam String description,
                                @RequestParam double discountPercent,
                                @RequestParam List<Long> serviceIds,
                                RedirectAttributes redirectAttributes) {
        if (discountPercent <= 0) {
            redirectAttributes.addFlashAttribute("error", "Discount cannot be a negative or 0%.");
            redirectAttributes.addFlashAttribute("prevName", name);
            redirectAttributes.addFlashAttribute("prevDescription", description);
            redirectAttributes.addFlashAttribute("prevDiscountPercent", discountPercent);
            redirectAttributes.addFlashAttribute("prevServiceIds", serviceIds);
            redirectAttributes.addFlashAttribute("openModal", "createPackageModal");
            return "redirect:/admin/packages";
        }
        try {
            packageService.create(name, description, discountPercent, serviceIds);
            redirectAttributes.addFlashAttribute("success", "Package created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("prevName", name);
            redirectAttributes.addFlashAttribute("prevDescription", description);
            redirectAttributes.addFlashAttribute("prevDiscountPercent", discountPercent);
            redirectAttributes.addFlashAttribute("prevServiceIds", serviceIds);
            redirectAttributes.addFlashAttribute("openModal", "createPackageModal");
        }
        return "redirect:/admin/packages";
    }

    @PostMapping("/{id}/update")
    public String updatePackage(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam double discountPercent,
                                @RequestParam List<Long> serviceIds,
                                RedirectAttributes redirectAttributes) {
        if (discountPercent <= 0) {
            redirectAttributes.addFlashAttribute("error", "Discount cannot be a negative or 0%.");
            return "redirect:/admin/packages";
        }
        packageService.update(id, name, description, discountPercent, serviceIds);
        return "redirect:/admin/packages";
    }

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id) {
        packageService.toggleActive(id);
        return "redirect:/admin/packages";
    }

    @PostMapping("/{id}/delete")
    public String deletePackage(@PathVariable Long id) {
        packageService.delete(id);
        return "redirect:/admin/packages";
    }
}
