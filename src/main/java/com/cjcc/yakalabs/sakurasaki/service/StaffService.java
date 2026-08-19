package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.model.Stylist;
import com.cjcc.yakalabs.sakurasaki.model.Therapist;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ReviewRepository;
import com.cjcc.yakalabs.sakurasaki.repository.StaffRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class StaffService {

    private final StaffRepository staffRepo;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepo;
    private final ReviewRepository reviewRepo;

    public StaffService(StaffRepository staffRepo, PasswordEncoder passwordEncoder,
                        AppointmentRepository appointmentRepo, ReviewRepository reviewRepo) {
        this.staffRepo = staffRepo;
        this.passwordEncoder = passwordEncoder;
        this.appointmentRepo = appointmentRepo;
        this.reviewRepo = reviewRepo;
    }

    // Consistent phone validation pattern (matches UserService)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    /**
     * Create staff — uses polymorphism: creates Stylist or Therapist
     * based on the staffType parameter. Auto-generates username and default password.
     */
    public Staff createStaff(String firstName, String lastName, String email, String phone,
                             String specialization, String staffType,
                             String workingDays, LocalTime startTime, LocalTime endTime) {
        Staff staff;
        switch (staffType != null ? staffType.toUpperCase() : "") {
            case "STYLIST":
                staff = new Stylist();
                break;
            case "THERAPIST":
                staff = new Therapist();
                break;
            default:
                staff = new Staff();
                break;
        }
        staff.setFirstName(firstName);
        staff.setLastName(lastName);
        staff.setEmail(email);
        // Validate phone format
        if (phone != null && !phone.isBlank()) {
            String cleanPhone = phone.replaceAll("[\\s-]", "");
            if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
                throw new RuntimeException("Please enter a valid 10-digit phone number.");
            }
            staff.setPhone(cleanPhone);
        }
        staff.setSpecialization(specialization);
        staff.setRole("ROLE_STAFF");
        staff.setEnabled(true);

        // Auto-generate username from name (e.g. "mika.honda") and default password
        String autoUsername = (firstName + "." + lastName).toLowerCase().replaceAll("[^a-z.]", "");
        staff.setUsername(autoUsername);
        staff.setPassword(passwordEncoder.encode("Staff@123"));

        staff.setWorkingDays(workingDays);
        staff.setStartTime(startTime != null ? startTime : LocalTime.of(9, 0));
        staff.setEndTime(endTime != null ? endTime : LocalTime.of(17, 0));
        return staffRepo.save(staff);
    }


    public List<Staff> findAll() {
        return staffRepo.findAll();
    }
    public Page<Staff> findAll(Pageable pageable) {
        return staffRepo.findAll(pageable);
    }

    public List<Staff> findActive() {
        return staffRepo.findByActive(true);
    }
    public Page<Staff> findActive(Pageable pageable) {
        return staffRepo.findByActive(true, pageable);
    }

    public Optional<Staff> findById(Long id) {
        return staffRepo.findById(id);
    }

    public List<Staff> searchByName(String keyword) {
        return staffRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
    }
    public Page<Staff> searchByName(String keyword, Pageable pageable) {
        return staffRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword, pageable);
    }

    public List<Staff> searchBySpecialty(String keyword) {
        return staffRepo.findBySpecializationContainingIgnoreCase(keyword);
    }
    public Page<Staff> searchBySpecialty(String keyword, Pageable pageable) {
        return staffRepo.findBySpecializationContainingIgnoreCase(keyword, pageable);
    }

    public Page<Staff> findByNameAndSpecialization(String name, String spec, Pageable pageable) {
        String nameFilter = (name != null && !name.isBlank()) ? name : null;
        String specFilter = (spec != null && !spec.isBlank()) ? spec : null;
        return staffRepo.findByNameAndSpecialization(nameFilter, specFilter, pageable);
    }

    public Staff updateStaff(Long id, String firstName, String lastName, String email, String phone,
                             String specialization, String workingDays, LocalTime startTime, LocalTime endTime) {
        Staff s = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));
        if (firstName != null && !firstName.isBlank()) s.setFirstName(firstName);
        if (lastName != null && !lastName.isBlank()) s.setLastName(lastName);
        if (email != null && !email.isBlank()) s.setEmail(email);
        // Validate phone format
        if (phone != null && !phone.isBlank()) {
            String cleanPhone = phone.replaceAll("[\\s-]", "");
            if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
                throw new RuntimeException("Please enter a valid 10-digit phone number.");
            }
            s.setPhone(cleanPhone);
        }
        if (specialization != null) s.setSpecialization(specialization);
        if (workingDays != null) s.setWorkingDays(workingDays);
        if (startTime != null) s.setStartTime(startTime);
        if (endTime != null) s.setEndTime(endTime);
        return staffRepo.save(s);
    }

    public void toggleActive(Long id) {
        Staff s = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));
        if (s.isActive() && !appointmentRepo.findByStaffId(id).isEmpty()) {
            throw new RuntimeException("Cannot disable staff member: they have existing appointments.");
        }
        s.setActive(!s.isActive());
        staffRepo.save(s);
    }

    @Transactional
    public void delete(Long id) {
        Staff staff = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));

        if (!appointmentRepo.findByStaffId(id).isEmpty()) {
            throw new RuntimeException("Cannot delete staff member: they have existing appointments.");
        }

        // Nullify staff reference in reviews instead of deleting them
        // This preserves historical review data and ratings
        reviewRepo.findByStaffId(id).forEach(review -> {
            review.setStaff(null);
            reviewRepo.save(review);
        });

        // Delete the staff itself
        staffRepo.delete(staff);
    }
}
