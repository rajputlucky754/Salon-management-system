package com.cjcc.yakalabs.sakurasaki.config;

import com.cjcc.yakalabs.sakurasaki.model.Admin;
import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.model.Customer;
import com.cjcc.yakalabs.sakurasaki.model.Review;
import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import com.cjcc.yakalabs.sakurasaki.model.ServicePackage;
import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.model.Stylist;
import com.cjcc.yakalabs.sakurasaki.model.Therapist;
import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import com.cjcc.yakalabs.sakurasaki.repository.CustomerRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ReviewRepository;
import com.cjcc.yakalabs.sakurasaki.repository.SalonServiceRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ServicePackageRepository;
import com.cjcc.yakalabs.sakurasaki.repository.StaffRepository;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Creates a predictable development dataset for Sakura Saki.
 *
 * Local H2 databases are reset on startup so UI demos and tests always start
 * from the same clean state. External databases are only reset when
 * app.seed.reset=true is explicitly provided.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;
    private final SalonServiceRepository salonServiceRepo;
    private final StaffRepository staffRepo;
    private final AppointmentRepository appointmentRepo;
    private final ServicePackageRepository packageRepo;
    private final ReviewRepository reviewRepo;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;
    private final boolean resetSeedData;

    public DataInitializer(UserRepository userRepo,
                           CustomerRepository customerRepo,
                           SalonServiceRepository salonServiceRepo,
                           StaffRepository staffRepo,
                           AppointmentRepository appointmentRepo,
                           ServicePackageRepository packageRepo,
                           ReviewRepository reviewRepo,
                           PasswordEncoder passwordEncoder,
                           Environment environment,
                           @Value("${app.seed.reset:false}") boolean resetSeedData) {
        this.userRepo = userRepo;
        this.customerRepo = customerRepo;
        this.salonServiceRepo = salonServiceRepo;
        this.staffRepo = staffRepo;
        this.appointmentRepo = appointmentRepo;
        this.packageRepo = packageRepo;
        this.reviewRepo = reviewRepo;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
        this.resetSeedData = resetSeedData;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (shouldResetDatabase()) {
            clearDatabase();
        } else {
            repairRoles();
        }

        if (userRepo.count() > 0 || salonServiceRepo.count() > 0) {
            System.out.println("Seed data already exists. Skipping sample creation.");
            return;
        }

        createAdmins();
        createCustomers();
        createServices();
        createStaff();
        createPackages();
        createAppointmentsAndReviews();

        System.out.println("Sample data ready: admin/admin, manager/manager, 4 customers, 4 staff, 9 services, 4 packages, 32 appointments, 12 reviews.");
    }

    private boolean shouldResetDatabase() {
        String datasourceUrl = environment.getProperty("spring.datasource.url", "");
        return resetSeedData || datasourceUrl.startsWith("jdbc:h2:");
    }

    private void clearDatabase() {
        reviewRepo.deleteAll();
        appointmentRepo.deleteAll();
        packageRepo.deleteAll();
        salonServiceRepo.deleteAll();
        userRepo.deleteAll();
        System.out.println("Database cleared before reseeding.");
    }

    private void repairRoles() {
        for (User user : userRepo.findAll()) {
            String correctRole = null;
            if (user instanceof Admin) {
                correctRole = "ROLE_ADMIN";
            } else if (user instanceof Staff) {
                correctRole = "ROLE_STAFF";
            } else if (user instanceof Customer) {
                correctRole = "ROLE_USER";
            }
            if (correctRole != null && !correctRole.equals(user.getRole())) {
                user.setRole(correctRole);
                userRepo.save(user);
            }
        }
    }

    private void createAdmins() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setEmail("admin@sakurasaki.com");
        admin.setFirstName("Airi");
        admin.setLastName("Kobayashi");
        admin.setPhone("0112001001");
        admin.setRole("ROLE_ADMIN");
        admin.setEnabled(true);
        admin.setAdminLevel("CEO");
        userRepo.save(admin);

        Admin manager = new Admin();
        manager.setUsername("manager");
        manager.setPassword(passwordEncoder.encode("Manager@123"));
        manager.setEmail("manager@sakurasaki.com");
        manager.setFirstName("Nori");
        manager.setLastName("Hayashi");
        manager.setPhone("0112001002");
        manager.setRole("ROLE_ADMIN");
        manager.setEnabled(true);
        manager.setAdminLevel("MANAGER");
        userRepo.save(manager);
    }

    private void createCustomers() {
        saveCustomer("sakura", "Sakura", "Tanaka", "sakura@example.com", "0771234567", 2450, "Gold");
        saveCustomer("yuki", "Yuki", "Sato", "yuki@example.com", "0779876543", 1180, "Silver");
        saveCustomer("hana", "Hana", "Suzuki", "hana@example.com", "0775551234", 620, "Bronze");
        saveCustomer("mei", "Mei", "Watanabe", "mei@example.com", "0772223344", 3400, "Platinum");
    }

    private void saveCustomer(String username,
                              String firstName,
                              String lastName,
                              String email,
                              String phone,
                              int loyaltyPoints,
                              String membershipTier) {
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(passwordEncoder.encode("User@123"));
        customer.setEmail(email);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhone(phone);
        customer.setRole("ROLE_USER");
        customer.setEnabled(true);
        customer.setLoyaltyPoints(loyaltyPoints);
        customer.setMembershipTier(membershipTier);
        userRepo.save(customer);
    }

    private void createServices() {
        saveService("Signature Haircut", "Precision cut, wash, blow dry, and soft finish.", 1800.00, 45, "Hair", "/images/stitch/hair-styling.jpg");
        saveService("Gloss Color Ritual", "Full color refresh with shine treatment and scalp care.", 5200.00, 105, "Hair", "/images/stitch/hair-color.jpg");
        saveService("Sakura Radiance Facial", "Deep cleanse, steam, mask, and calming facial massage.", 3600.00, 70, "Skin", "/images/stitch/skin-therapy.jpg");
        saveService("Hydration Facial", "Moisture-focused facial for dry or tired skin.", 3200.00, 60, "Skin", "/images/stitch/facial-care.jpg");
        saveService("Botanical Manicure", "Nail shaping, cuticle care, polish, and hand massage.", 1500.00, 45, "Nails", "/images/stitch/nail-care.jpg");
        saveService("Gel Pedicure", "Foot soak, exfoliation, gel polish, and heel care.", 2400.00, 60, "Nails", "/images/stitch/pedicure.jpg");
        saveService("Aromatherapy Massage", "Relaxing full-body massage with calming essential oils.", 5600.00, 75, "Spa", "/images/stitch/massage.jpg");
        saveService("Bridal Glow Makeup", "Long-wear bridal makeup with lashes and final touch-up.", 9500.00, 135, "Bridal", "/images/stitch/bridal-makeup.jpg");
        saveService("Brow Shaping", "Brow mapping, shaping, trimming, and soothing finish.", 900.00, 25, "Beauty", "/images/stitch/brow-care.jpg");
    }

    private void saveService(String name,
                             String description,
                             double price,
                             int durationMinutes,
                             String category,
                             String imageUrl) {
        SalonService service = new SalonService(name, description, price, durationMinutes, category);
        service.setImageUrl(imageUrl);
        salonServiceRepo.save(service);
    }

    private void createStaff() {
        Stylist mika = new Stylist();
        mika.setUsername("mika.honda");
        mika.setPassword(passwordEncoder.encode("Staff@123"));
        mika.setEmail("mika@sakurasaki.com");
        mika.setFirstName("Mika");
        mika.setLastName("Honda");
        mika.setPhone("0111234567");
        mika.setRole("ROLE_STAFF");
        mika.setEnabled(true);
        mika.setSpecialization("Cuts, styling, and everyday hair design");
        mika.setWorkingDays("MON,TUE,WED,THU,FRI");
        mika.setStartTime(LocalTime.of(9, 0));
        mika.setEndTime(LocalTime.of(17, 0));
        mika.setCertificationLevel("Senior");
        staffRepo.save(mika);

        Stylist koji = new Stylist();
        koji.setUsername("koji.yamamoto");
        koji.setPassword(passwordEncoder.encode("Staff@123"));
        koji.setEmail("koji@sakurasaki.com");
        koji.setFirstName("Koji");
        koji.setLastName("Yamamoto");
        koji.setPhone("0119876543");
        koji.setRole("ROLE_STAFF");
        koji.setEnabled(true);
        koji.setSpecialization("Color correction and gloss rituals");
        koji.setWorkingDays("MON,WED,FRI,SAT");
        koji.setStartTime(LocalTime.of(10, 0));
        koji.setEndTime(LocalTime.of(18, 0));
        koji.setCertificationLevel("Master");
        staffRepo.save(koji);

        Therapist yui = new Therapist();
        yui.setUsername("yui.nakamura");
        yui.setPassword(passwordEncoder.encode("Staff@123"));
        yui.setEmail("yui@sakurasaki.com");
        yui.setFirstName("Yui");
        yui.setLastName("Nakamura");
        yui.setPhone("0115554321");
        yui.setRole("ROLE_STAFF");
        yui.setEnabled(true);
        yui.setSpecialization("Facials and skin care");
        yui.setWorkingDays("TUE,THU,FRI,SAT");
        yui.setStartTime(LocalTime.of(9, 0));
        yui.setEndTime(LocalTime.of(16, 0));
        yui.setTherapyType("Aromatherapy");
        staffRepo.save(yui);

        Therapist aiko = new Therapist();
        aiko.setUsername("aiko.mori");
        aiko.setPassword(passwordEncoder.encode("Staff@123"));
        aiko.setEmail("aiko@sakurasaki.com");
        aiko.setFirstName("Aiko");
        aiko.setLastName("Mori");
        aiko.setPhone("0113338899");
        aiko.setRole("ROLE_STAFF");
        aiko.setEnabled(true);
        aiko.setSpecialization("Massage, nails, and bridal prep");
        aiko.setWorkingDays("MON,TUE,THU,SAT,SUN");
        aiko.setStartTime(LocalTime.of(11, 0));
        aiko.setEndTime(LocalTime.of(19, 0));
        aiko.setTherapyType("Wellness");
        staffRepo.save(aiko);
    }

    private void createPackages() {
        Map<String, SalonService> services = salonServiceRepo.findAll().stream()
                .collect(Collectors.toMap(SalonService::getName, Function.identity()));

        savePackage("Bridal Bliss", "Makeup, glow facial, hair finish, and nails for wedding day prep.", 15.0,
                services.get("Bridal Glow Makeup"),
                services.get("Sakura Radiance Facial"),
                services.get("Signature Haircut"),
                services.get("Botanical Manicure"));

        savePackage("Pamper Day", "A restorative spa day with facial, massage, and pedicure.", 12.0,
                services.get("Hydration Facial"),
                services.get("Aromatherapy Massage"),
                services.get("Gel Pedicure"));

        savePackage("Color Refresh", "Color, cut, and brow shaping for a polished refresh.", 10.0,
                services.get("Gloss Color Ritual"),
                services.get("Signature Haircut"),
                services.get("Brow Shaping"));

        savePackage("Sakura Essentials", "Quick glow basics for recurring customer visits.", 8.0,
                services.get("Sakura Radiance Facial"),
                services.get("Botanical Manicure"),
                services.get("Brow Shaping"));
    }

    private void savePackage(String name, String description, double discountPercent, SalonService... services) {
        ServicePackage servicePackage = new ServicePackage(name, description, discountPercent);
        servicePackage.setServices(Set.of(services));
        packageRepo.save(servicePackage);
    }

    private void createAppointmentsAndReviews() {
        Map<String, Customer> customers = customerRepo.findAll().stream()
                .collect(Collectors.toMap(Customer::getUsername, Function.identity()));
        Map<String, SalonService> services = salonServiceRepo.findAll().stream()
                .collect(Collectors.toMap(SalonService::getName, Function.identity()));
        Map<String, Staff> staff = staffRepo.findAll().stream()
                .collect(Collectors.toMap(Staff::getUsername, Function.identity()));

        LocalDate today = LocalDate.now();

        Appointment a1 = saveAppointment(customers.get("sakura"), services.get("Signature Haircut"), staff.get("mika.honda"), today.plusDays(1), LocalTime.of(9, 0), "SCHEDULED", "Requested soft layers and volume.");
        saveAppointment(customers.get("yuki"), services.get("Gloss Color Ritual"), staff.get("koji.yamamoto"), today, LocalTime.of(10, 30), "SCHEDULED", "Patch test completed.");
        saveAppointment(customers.get("hana"), services.get("Hydration Facial"), staff.get("yui.nakamura"), today, LocalTime.of(13, 0), "SCHEDULED", "Sensitive skin notes added.");
        saveAppointment(customers.get("mei"), services.get("Bridal Glow Makeup"), staff.get("aiko.mori"), today.plusDays(2), LocalTime.of(11, 0), "SCHEDULED", "Trial look: soft rose palette.");
        saveAppointment(customers.get("sakura"), services.get("Botanical Manicure"), staff.get("aiko.mori"), today.plusDays(4), LocalTime.of(15, 30), "SCHEDULED", "Use neutral pink polish.");
        saveAppointment(customers.get("yuki"), services.get("Brow Shaping"), staff.get("aiko.mori"), today.plusDays(5), LocalTime.of(12, 0), "SCHEDULED", "Keep natural arch.");
        saveAppointment(customers.get("hana"), services.get("Signature Haircut"), staff.get("mika.honda"), today.plusDays(2), LocalTime.of(10, 0), "SCHEDULED", "Short bob cleanup.");
        saveAppointment(customers.get("mei"), services.get("Signature Haircut"), staff.get("mika.honda"), today.plusDays(6), LocalTime.of(14, 30), "SCHEDULED", "Polished blow dry after cut.");
        saveAppointment(customers.get("sakura"), services.get("Gloss Color Ritual"), staff.get("koji.yamamoto"), today.plusDays(3), LocalTime.of(9, 30), "SCHEDULED", "Refresh warm brown tone.");
        saveAppointment(customers.get("hana"), services.get("Gloss Color Ritual"), staff.get("koji.yamamoto"), today.plusDays(7), LocalTime.of(11, 0), "SCHEDULED", "Root touch-up and gloss.");
        saveAppointment(customers.get("yuki"), services.get("Sakura Radiance Facial"), staff.get("yui.nakamura"), today.plusDays(1), LocalTime.of(15, 0), "SCHEDULED", "Focus on calming redness.");
        saveAppointment(customers.get("mei"), services.get("Hydration Facial"), staff.get("yui.nakamura"), today.plusDays(4), LocalTime.of(10, 30), "SCHEDULED", "Hydrating mask preference.");
        saveAppointment(customers.get("sakura"), services.get("Aromatherapy Massage"), staff.get("aiko.mori"), today.plusDays(6), LocalTime.of(16, 0), "SCHEDULED", "Relaxing lavender blend.");
        saveAppointment(customers.get("hana"), services.get("Gel Pedicure"), staff.get("aiko.mori"), today.plusDays(8), LocalTime.of(13, 30), "SCHEDULED", "Cherry blossom nail art.");
        saveAppointment(customers.get("mei"), services.get("Brow Shaping"), staff.get("aiko.mori"), today.plusDays(9), LocalTime.of(11, 30), "SCHEDULED", "Soft bridal brow shaping.");
        saveAppointment(customers.get("yuki"), services.get("Botanical Manicure"), staff.get("aiko.mori"), today.plusDays(10), LocalTime.of(17, 0), "SCHEDULED", "Classic nude polish.");

        Appointment c1 = saveAppointment(customers.get("sakura"), services.get("Sakura Radiance Facial"), staff.get("yui.nakamura"), today.minusDays(3), LocalTime.of(10, 0), "COMPLETED", "Customer loved the calming mask.");
        Appointment c2 = saveAppointment(customers.get("yuki"), services.get("Signature Haircut"), staff.get("mika.honda"), today.minusDays(4), LocalTime.of(14, 0), "COMPLETED", "Trim and blow dry completed.");
        Appointment c3 = saveAppointment(customers.get("hana"), services.get("Aromatherapy Massage"), staff.get("aiko.mori"), today.minusDays(7), LocalTime.of(16, 0), "COMPLETED", "Lavender oil preference.");
        Appointment c4 = saveAppointment(customers.get("mei"), services.get("Gloss Color Ritual"), staff.get("koji.yamamoto"), today.minusDays(8), LocalTime.of(9, 30), "COMPLETED", "Gloss color: warm chestnut.");
        Appointment c5 = saveAppointment(customers.get("sakura"), services.get("Gel Pedicure"), staff.get("aiko.mori"), today.minusDays(12), LocalTime.of(11, 30), "COMPLETED", "Long-lasting gel finish.");
        Appointment c6 = saveAppointment(customers.get("mei"), services.get("Bridal Glow Makeup"), staff.get("aiko.mori"), today.minusDays(15), LocalTime.of(13, 0), "COMPLETED", "Trial appointment completed.");
        Appointment c7 = saveAppointment(customers.get("hana"), services.get("Signature Haircut"), staff.get("mika.honda"), today.minusDays(10), LocalTime.of(9, 0), "COMPLETED", "Regular fringe trim.");
        Appointment c8 = saveAppointment(customers.get("sakura"), services.get("Signature Haircut"), staff.get("mika.honda"), today.minusDays(18), LocalTime.of(15, 0), "COMPLETED", "Layered cut maintenance.");
        Appointment c9 = saveAppointment(customers.get("yuki"), services.get("Gloss Color Ritual"), staff.get("koji.yamamoto"), today.minusDays(11), LocalTime.of(10, 30), "COMPLETED", "Cool ash gloss completed.");
        Appointment c10 = saveAppointment(customers.get("hana"), services.get("Brow Shaping"), staff.get("koji.yamamoto"), today.minusDays(16), LocalTime.of(16, 0), "COMPLETED", "Brow tidy before event.");
        Appointment c11 = saveAppointment(customers.get("mei"), services.get("Hydration Facial"), staff.get("yui.nakamura"), today.minusDays(13), LocalTime.of(12, 30), "COMPLETED", "Extra hydration serum used.");
        Appointment c12 = saveAppointment(customers.get("yuki"), services.get("Sakura Radiance Facial"), staff.get("yui.nakamura"), today.minusDays(20), LocalTime.of(11, 0), "COMPLETED", "Post-travel skin recovery.");

        saveAppointment(customers.get("hana"), services.get("Botanical Manicure"), staff.get("aiko.mori"), today.minusDays(1), LocalTime.of(9, 30), "CANCELLED", "Customer requested reschedule.");
        saveAppointment(customers.get("yuki"), services.get("Hydration Facial"), staff.get("yui.nakamura"), today.plusDays(3), LocalTime.of(14, 30), "CANCELLED", "Staff unavailable.");
        saveAppointment(customers.get("mei"), services.get("Signature Haircut"), staff.get("mika.honda"), today.minusDays(2), LocalTime.of(16, 30), "CANCELLED", "Customer postponed due to travel.");
        saveAppointment(customers.get("sakura"), services.get("Gloss Color Ritual"), staff.get("koji.yamamoto"), today.plusDays(5), LocalTime.of(15, 30), "CANCELLED", "Patch test needs repeating.");

        saveReview(c1, 5, "The facial felt peaceful and my skin looked fresh immediately.");
        saveReview(c2, 5, "Mika understood exactly what I wanted. Beautiful finish.");
        saveReview(c3, 4, "Very relaxing massage and lovely atmosphere.");
        saveReview(c4, 5, "Koji made the color look natural and glossy.");
        saveReview(c5, 4, "Clean work, neat polish, and friendly service.");
        saveReview(c6, 5, "The bridal trial gave me so much confidence.");
        saveReview(c7, 5, "Mika keeps my haircut neat and easy to manage.");
        saveReview(c8, 4, "Lovely layers and quick service.");
        saveReview(c9, 5, "Koji balanced the color perfectly.");
        saveReview(c10, 4, "My brows looked tidy without feeling too sharp.");
        saveReview(c11, 5, "Yui gave my dry skin a real reset.");
        saveReview(c12, 5, "Calming, gentle, and exactly what I needed.");

        // Keep the variable used so future edits notice this is the primary sample user's next visit.
        a1.setNotes(a1.getNotes() + " Primary dashboard sample.");
        appointmentRepo.save(a1);
    }

    private Appointment saveAppointment(Customer customer,
                                        SalonService service,
                                        Staff staff,
                                        LocalDate date,
                                        LocalTime time,
                                        String status,
                                        String notes) {
        Appointment appointment = new Appointment(customer, service, staff, date, time);
        appointment.setStatus(status);
        appointment.setNotes(notes);
        return appointmentRepo.save(appointment);
    }

    private void saveReview(Appointment appointment, int rating, String comment) {
        Review review = new Review(
                appointment.getCustomer(),
                appointment.getService(),
                appointment.getStaff(),
                appointment,
                rating,
                comment
        );
        reviewRepo.save(review);
    }
}
