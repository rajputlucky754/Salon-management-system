package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.StaffService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffBrowseController {

    private final StaffService staffService;

    public StaffBrowseController(StaffService staffService) {
        this.staffService = staffService;
    }

    private boolean isRealUser(Authentication auth) {
        return auth != null && auth.isAuthenticated()
                && auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"));
    }

    @GetMapping("/staff")
    public String browseStaff(Authentication auth, Model model) {
        model.addAttribute("staffList", staffService.findActive());
        
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
        
        return "public/staff";
    }
}
