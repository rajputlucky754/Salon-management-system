package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Admin;
import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    // The username of the permanent super admin that cannot be demoted/deactivated/deleted
    private static final String SUPER_ADMIN_USERNAME = "admin";

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isSuperAdmin(Long id) {
        return userRepo.findById(id)
                .map(u -> SUPER_ADMIN_USERNAME.equals(u.getUsername()))
                .orElse(false);
    }

    public boolean isSuperAdmin(String username) {
        return SUPER_ADMIN_USERNAME.equals(username);
    }

    // CREATE — register a new admin user with adminLevel
    public User createAdmin(String username, String password, String email, String adminLevel) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setEmail(email);
        admin.setRole("ROLE_ADMIN");
        admin.setEnabled(true);
        admin.setAdminLevel(adminLevel != null ? adminLevel : "ADMIN");
        return userRepo.save(admin);
    }

    // Backward-compatible create
    public User createAdmin(String username, String password, String email) {
        return createAdmin(username, password, email, "ADMIN");
    }

    // READ — list all admin users
    public Page<User> listAdmins(Pageable pageable) {
        return userRepo.findByRole("ROLE_ADMIN", pageable);
    }

    // READ — find admin by ID
    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    // READ — list all users
    public Page<User> listAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

    // READ — search users by username
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepo.findByUsernameContainingIgnoreCase(keyword, pageable);
    }

    // READ — list staff
    public List<User> listStaff() {
        return userRepo.findByRole("ROLE_STAFF");
    }

    // UPDATE — update admin details
    public User updateAdmin(Long id, String username, String email, String adminLevel) {
        User existing = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        if (username != null && !username.isBlank()) existing.setUsername(username);
        if (email != null && !email.isBlank()) existing.setEmail(email);
        if (existing instanceof Admin a && adminLevel != null) {
            a.setAdminLevel(adminLevel);
        }
        return userRepo.save(existing);
    }

    // Backward compatible
    public User updateAdmin(Long id, String username, String email) {
        return updateAdmin(id, username, email, null);
    }

    // UPDATE — change role (protected: cannot demote super admin)
    public User changeRole(Long id, String newRole) {
        if (isSuperAdmin(id)) {
            throw new RuntimeException("The primary admin account cannot be demoted.");
        }
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setRole(newRole);
        return userRepo.save(user);
    }

    // UPDATE — toggle enabled/disabled (protected: cannot disable super admin)
    public User toggleEnabled(Long id) {
        if (isSuperAdmin(id)) {
            throw new RuntimeException("The primary admin account cannot be disabled.");
        }
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setEnabled(!user.isEnabled());
        return userRepo.save(user);
    }

    // DELETE — deactivate (soft delete) an admin (protected)
    public void deactivateAdmin(Long id) {
        if (isSuperAdmin(id)) {
            throw new RuntimeException("The primary admin account cannot be deactivated.");
        }
        User admin = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        admin.setEnabled(false);
        userRepo.save(admin);
    }

    /**
     * Check if the requesting admin (by username) has higher or equal level than target admin.
     * Safely handles users with ROLE_ADMIN who are not Admin entities (role-promoted users).
     */
    public boolean canManage(String currentUsername, Long targetAdminId) {
        User current = userRepo.findByUsername(currentUsername).orElse(null);
        User target = userRepo.findById(targetAdminId).orElse(null);
        if (current == null || target == null) return false;
        if (!(target instanceof Admin targetAdmin)) return true; // non-Admin entity can always be managed

        // If current user is not an Admin entity, treat them as basic-level ADMIN
        if (!(current instanceof Admin currentAdmin)) {
            // Role-promoted user can only manage basic-level admins
            return "ADMIN".equals(targetAdmin.getAdminLevel());
        }

        return currentAdmin.hasLevelOrHigher(targetAdmin.getAdminLevel());
    }
}
