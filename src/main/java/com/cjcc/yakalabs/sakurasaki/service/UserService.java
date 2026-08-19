package com.cjcc.yakalabs.sakurasaki.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cjcc.yakalabs.sakurasaki.model.Customer;
import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    // Validation patterns
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    public UserService(UserRepository userRepository, AppointmentRepository appointmentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new customer user with full validation.
     */
    public Customer registerCustomer(String username, String rawPassword, String email,
                                      String firstName, String lastName, String phone) {
        // Username validation
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new RuntimeException("Username must be 3-30 characters, using only letters, numbers, and underscores.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username '" + username + "' is already taken. Please choose another.");
        }

        // Email validation
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("Please enter a valid email address (e.g., name@example.com).");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("An account with this email already exists. Please use a different email or sign in.");
        }

        // Password validation
        if (rawPassword == null || !PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new RuntimeException("Password must be at least 8 characters with uppercase, lowercase, digit, and special character (@#$%^&+=!).");
        }

        // Phone validation
        if (phone == null || phone.isBlank()) {
            throw new RuntimeException("Phone number is required.");
        }
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new RuntimeException("Please enter a valid phone number.");
        }

        // Name validation
        if (firstName == null || firstName.isBlank()) {
            throw new RuntimeException("First name is required.");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new RuntimeException("Last name is required.");
        }

        // Create Customer (which IS a User via inheritance)
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(passwordEncoder.encode(rawPassword));
        customer.setEmail(email);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhone(cleanPhone);
        customer.setRole("ROLE_USER");
        customer.setEnabled(true);
        return userRepository.save(customer);
    }

    /**
     * Legacy method for creating plain User (admin use).
     */
    public User registerUser(String username, String rawPassword, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Promote a user to admin role.
     * Note: This changes the security role but the discriminator remains unchanged.
     * For full Admin entity features (e.g., adminLevel), use AdminService.createAdmin instead.
     */
    public void makeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
                
        if (user instanceof Customer) {
            throw new RuntimeException("Cannot promote a Customer to Admin directly due to data integrity constraints. Please create a separate Admin account.");
        }
        if (user instanceof Staff) {
            throw new RuntimeException("Cannot promote a Staff member to Admin directly. Staff entity type is incompatible. Please create a separate Admin account.");
        }
        
        user.setRole("ROLE_ADMIN");
        userRepository.save(user);
    }

    // ---- Customer-specific methods (Member 1 — Customer & Authentication Module) ----

    /**
     * Find a user by username.
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Find a user by ID.
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    /**
     * List all customers (users with ROLE_USER).
     */
    public List<User> findAllCustomers() {
        return userRepository.findByRole("ROLE_USER");
    }
    public Page<User> findAllCustomers(Pageable pageable) {
        return userRepository.findByRole("ROLE_USER", pageable);
    }

    /**
     * Search customers by username keyword.
     */
    public List<User> searchCustomers(String keyword) {
        return userRepository.findByUsernameContainingIgnoreCase(keyword);
    }
    public Page<User> searchCustomers(String keyword, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(keyword, pageable);
    }

    /**
     * Update a customer's profile (name, phone, email change with validation).
     * OOP: Encapsulation — only exposes controlled update, not raw field access.
     */
    public User updateProfile(String username, String firstName, String lastName,
                               String phone, String newPassword) {
        User user = findByUsername(username);

        // First name validation
        if (firstName != null && !firstName.isBlank()) {
            user.setFirstName(firstName);
        }

        // Last name validation
        if (lastName != null && !lastName.isBlank()) {
            user.setLastName(lastName);
        }

        // Phone validation
        if (phone != null && !phone.isBlank()) {
            String cleanPhone = phone.replaceAll("[\\s-]", "");
            if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
                throw new RuntimeException("Please enter a valid phone number.");
            }
            user.setPhone(cleanPhone);
        }

        // Password update (optional — only if provided)
        if (newPassword != null && !newPassword.isBlank()) {
            if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                throw new RuntimeException("Password must be at least 8 characters with uppercase, lowercase, digit, and special character (@#$%^&+=!).");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }

    /**
     * Deactivate a user account (soft delete).
     */
    public void deactivateUser(Long id) {
        User user = findById(id);
        user.setEnabled(false);
        userRepository.save(user);
    }

    /**
     * Activate a user account.
     */
    public void activateUser(Long id) {
        User user = findById(id);
        user.setEnabled(true);
        userRepository.save(user);
    }

    /**
     * Toggle user enabled/disabled status.
     */
    public void toggleEnabled(Long id) {
        User user = findById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    /**
     * Deletes a user account (Soft Delete & Anonymization).
     */
    public void deleteUser(Long id) {
        User user = findById(id);
        // Soft delete: disable account and anonymize personal data
        // This preserves historical appointments and reviews for reporting
        user.setEnabled(false);
        user.setFirstName("Deleted");
        user.setLastName("User");
        user.setPhone(null);
        // Scramble username and email to free up uniqueness constraints
        String anonymizedSuffix = "_deleted_" + user.getId();
        user.setUsername("deleted_user" + anonymizedSuffix);
        user.setEmail("deleted" + anonymizedSuffix + "@removed.local");
        userRepository.save(user);
    }
}