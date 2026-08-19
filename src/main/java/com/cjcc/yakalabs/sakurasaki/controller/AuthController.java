package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phone,
            Model model
    ) {
        try {
            // Validate phone: exactly 10 digits
            if (phone == null || !phone.matches("\\d{10}")) {
                throw new RuntimeException("Phone number must be exactly 10 digits.");
            }
            userService.registerCustomer(username, password, email, firstName, lastName, phone);
            model.addAttribute("success", "Registration successful! Please sign in.");
            return "auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            // Preserve form values
            model.addAttribute("prevUsername", username);
            model.addAttribute("prevEmail", email);
            model.addAttribute("prevFirstName", firstName);
            model.addAttribute("prevLastName", lastName);
            model.addAttribute("prevPhone", phone);
            return "auth/register";
        }
    }
}