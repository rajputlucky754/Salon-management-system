package com.cjcc.yakalabs.sakurasaki;

import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import com.cjcc.yakalabs.sakurasaki.repository.CustomerRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ReviewRepository;
import com.cjcc.yakalabs.sakurasaki.repository.SalonServiceRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ServicePackageRepository;
import com.cjcc.yakalabs.sakurasaki.repository.StaffRepository;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SakuraSakiApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SalonServiceRepository salonServiceRepository;

    @Autowired
    private ServicePackageRepository servicePackageRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void sampleDataIsCreatedForDevelopmentAndUiTests() {
        assertEquals(10, userRepository.count());
        assertEquals(4, customerRepository.count());
        assertEquals(4, staffRepository.count());
        assertEquals(9, salonServiceRepository.count());
        assertEquals(4, servicePackageRepository.count());
        assertEquals(32, appointmentRepository.count());
        assertEquals(12, reviewRepository.count());

        assertTrue(userRepository.findByUsername("admin").isPresent());
        assertTrue(userRepository.findByUsername("sakura").isPresent());
        assertTrue(userRepository.findByUsername("mika.honda").isPresent());
        assertEquals(16, appointmentRepository.findByStatus("SCHEDULED").size());
        assertEquals(12, appointmentRepository.findByStatus("COMPLETED").size());
        assertEquals(4, appointmentRepository.findByStatus("CANCELLED").size());
        staffRepository.findAll().forEach(staff ->
                assertTrue(appointmentRepository.findByStaffId(staff.getId()).size() >= 6)
        );
    }
}
