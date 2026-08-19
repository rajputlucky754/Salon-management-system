package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.SalonServiceService;
import com.cjcc.yakalabs.sakurasaki.service.ServicePackageService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.cjcc.yakalabs.sakurasaki.service.FileStorageService;

import java.util.List;

@Controller
@RequestMapping("/admin/services")
public class ServiceManagementController {

    private final SalonServiceService salonServiceService;
    private final FileStorageService fileStorageService;

    public ServiceManagementController(SalonServiceService salonServiceService, FileStorageService fileStorageService) {
        this.salonServiceService = salonServiceService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String listServices(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String category,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Authentication auth, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        model.addAttribute("username", auth.getName());
        
        Page<com.cjcc.yakalabs.sakurasaki.model.SalonService> services = salonServiceService.findByNameAndCategory(name, category, pageable);
        
        model.addAttribute("services", services);
        model.addAttribute("name", name);
        model.addAttribute("category", category);
        return "admin/services";
    }

    @PostMapping("/create")
    public String createService(@RequestParam String name,
                                @RequestParam String description,
                                @RequestParam double price,
                                @RequestParam int durationMinutes,
                                @RequestParam(required = false) String category,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        if (price <= 0) {
            redirectAttributes.addFlashAttribute("error", "Price must be greater than 0.");
            redirectAttributes.addFlashAttribute("prevName", name);
            redirectAttributes.addFlashAttribute("prevDescription", description);
            redirectAttributes.addFlashAttribute("prevPrice", price);
            redirectAttributes.addFlashAttribute("prevDuration", durationMinutes);
            redirectAttributes.addFlashAttribute("prevCategory", category);
            redirectAttributes.addFlashAttribute("openModal", "createServiceModal");
            return "redirect:/admin/services";
        }
        if (durationMinutes < 15) {
            redirectAttributes.addFlashAttribute("error", "Duration must be at least 15 minutes.");
            redirectAttributes.addFlashAttribute("prevName", name);
            redirectAttributes.addFlashAttribute("prevDescription", description);
            redirectAttributes.addFlashAttribute("prevPrice", price);
            redirectAttributes.addFlashAttribute("prevDuration", durationMinutes);
            redirectAttributes.addFlashAttribute("prevCategory", category);
            redirectAttributes.addFlashAttribute("openModal", "createServiceModal");
            return "redirect:/admin/services";
        }
        try {
            String imageUrl = fileStorageService.storeFile(imageFile);
            salonServiceService.create(name, description, price, durationMinutes, category, imageUrl);
            redirectAttributes.addFlashAttribute("success", "Service created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("prevName", name);
            redirectAttributes.addFlashAttribute("prevDescription", description);
            redirectAttributes.addFlashAttribute("prevPrice", price);
            redirectAttributes.addFlashAttribute("prevDuration", durationMinutes);
            redirectAttributes.addFlashAttribute("prevCategory", category);
            redirectAttributes.addFlashAttribute("openModal", "createServiceModal");
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/update")
    public String updateService(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam double price,
                                @RequestParam int durationMinutes,
                                @RequestParam(required = false) String category,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        if (price <= 0) {
            redirectAttributes.addFlashAttribute("error", "Price must be greater than 0.");
            return "redirect:/admin/services";
        }
        if (durationMinutes < 15) {
            redirectAttributes.addFlashAttribute("error", "Duration must be at least 15 minutes.");
            return "redirect:/admin/services";
        }
        try {
            String imageUrl = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = fileStorageService.storeFile(imageFile);
            }
            salonServiceService.update(id, name, description, price, durationMinutes, category, imageUrl);
            redirectAttributes.addFlashAttribute("success", "Service updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update service: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id) {
        salonServiceService.toggleActive(id);
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/delete")
    public String deleteService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            salonServiceService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Service deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete service: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }
}
