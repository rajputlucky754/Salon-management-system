package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.*;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import com.cjcc.yakalabs.sakurasaki.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final AppointmentService appointmentService;
    private final CustomerService customerService;
    private final UserRepository userRepo;

    public ReviewController(ReviewService reviewService, AppointmentService appointmentService,
                            CustomerService customerService, UserRepository userRepo) {
        this.reviewService = reviewService;
        this.appointmentService = appointmentService;
        this.customerService = customerService;
        this.userRepo = userRepo;
    }

    @GetMapping("/submit/{appointmentId}")
    public String reviewForm(@PathVariable Long appointmentId, Authentication auth, Model model) {
        Appointment a = appointmentService.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        model.addAttribute("appointment", a);
        model.addAttribute("username", auth.getName());
        model.addAttribute("existingReview", reviewService.findByAppointmentId(appointmentId).orElse(null));
        return "review/submit";
    }

    @PostMapping("/submit")
    public String submitReview(@RequestParam Long appointmentId,
                               @RequestParam int rating,
                               @RequestParam(required = false) String comment,
                               RedirectAttributes ra) {
        try {
            reviewService.submitReview(appointmentId, rating, comment);
            ra.addFlashAttribute("success", "Review submitted!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/bookings";
    }

    @GetMapping("/service/{serviceId}")
    public String serviceReviews(@PathVariable Long serviceId, Model model) {
        model.addAttribute("reviews", reviewService.getReviewsForService(serviceId));
        model.addAttribute("avgRating", reviewService.getAverageRatingForService(serviceId));
        model.addAttribute("serviceId", serviceId);
        return "review/service-reviews";
    }

    @PostMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        try {
            User user = userRepo.findByUsername(auth.getName()).orElseThrow();
            Customer customer = customerService.findByEmail(user.getEmail()).orElseThrow();
            Review review = reviewService.findById(id).orElseThrow();
            if (!review.getCustomer().getId().equals(customer.getId())) {
                throw new RuntimeException("You can only delete your own reviews.");
            }
            reviewService.deleteReview(id);
            ra.addFlashAttribute("success", "Review deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/bookings";
    }
    @PostMapping("/{id}/edit")
    public String editReview(@PathVariable Long id,
                             @RequestParam int rating,
                             @RequestParam(required = false) String comment,
                             Authentication auth,
                             RedirectAttributes ra) {
        try {
            User user = userRepo.findByUsername(auth.getName()).orElseThrow();
            Customer customer = customerService.findByEmail(user.getEmail()).orElseThrow();
            reviewService.editReview(id, customer.getId(), rating, comment);
            ra.addFlashAttribute("success", "Review updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/bookings";
    }
}
