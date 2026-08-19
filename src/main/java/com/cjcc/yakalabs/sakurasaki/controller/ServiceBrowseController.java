package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.repository.ReviewRepository;
import com.cjcc.yakalabs.sakurasaki.service.SalonServiceService;
import com.cjcc.yakalabs.sakurasaki.service.ServicePackageService;
import com.cjcc.yakalabs.sakurasaki.service.StaffService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ServiceBrowseController {

    private final SalonServiceService salonServiceService;
    private final ServicePackageService packageService;
    private final StaffService staffService;
    private final ReviewRepository reviewRepo;

    public ServiceBrowseController(SalonServiceService salonServiceService,
                                    ServicePackageService packageService,
                                    StaffService staffService,
                                    ReviewRepository reviewRepo) {
        this.salonServiceService = salonServiceService;
        this.packageService = packageService;
        this.staffService = staffService;
        this.reviewRepo = reviewRepo;
    }

    private boolean isRealUser(Authentication auth) {
        return auth != null && auth.isAuthenticated()
                && auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"));
    }

    private void addAuthState(Authentication auth, Model model) {
        boolean loggedIn = isRealUser(auth);
        model.addAttribute("isLoggedIn", loggedIn);
        model.addAttribute("isAdmin", false);
        model.addAttribute("isStaff", false);

        if (loggedIn) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
            model.addAttribute("isStaff", auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF")));
        }
    }

    @GetMapping("/services")
    public String browseServices(@RequestParam(required = false) String search,
                                  @RequestParam(required = false) String q,
                                  @RequestParam(required = false) String category,
                                  Authentication auth, Model model) {
        // Accept both 'search' and 'q' params (from nav search bar)
        String query = (q != null && !q.isBlank()) ? q : search;

        var allServices = salonServiceService.findActive();

        if (query != null && !query.isBlank()) {
            allServices = allServices.stream()
                    .filter(s -> s.getName().toLowerCase().contains(query.toLowerCase())
                            || (s.getDescription() != null && s.getDescription().toLowerCase().contains(query.toLowerCase())))
                    .toList();
            model.addAttribute("search", query);
        }

        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("all")) {
            allServices = allServices.stream()
                    .filter(s -> s.getCategory() != null && s.getCategory().equalsIgnoreCase(category))
                    .toList();
            model.addAttribute("activeCategory", category);
        }

        model.addAttribute("services", allServices);
        model.addAttribute("packages", packageService.findActive());

        // All unique categories for filter pills
        var categories = salonServiceService.findActive().stream()
                .map(s -> s.getCategory())
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .toList();
        model.addAttribute("categories", categories);

        addAuthState(auth, model);

        return "public/services";
    }

    @GetMapping("/services/{id}")
    public String serviceDetail(@PathVariable Long id, Authentication auth, Model model) {
        var service = salonServiceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        model.addAttribute("service", service);

        // Load reviews for this service
        model.addAttribute("reviews", reviewRepo.findByServiceIdAndVisible(id, true));
        model.addAttribute("avgRating", reviewRepo.averageRatingByServiceId(id));

        // Load staff for inline booking
        model.addAttribute("staffList", staffService.findActive());

        addAuthState(auth, model);

        return "public/service-detail";
    }

    @GetMapping("/services/package/{id}")
    public String packageDetail(@PathVariable Long id, Authentication auth, Model model) {
        var pkg = packageService.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        model.addAttribute("pkg", pkg);

        addAuthState(auth, model);

        return "public/package-detail";
    }
}
