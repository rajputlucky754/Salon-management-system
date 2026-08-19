package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import com.cjcc.yakalabs.sakurasaki.model.ServicePackage;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ReviewRepository;
import com.cjcc.yakalabs.sakurasaki.repository.SalonServiceRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ServicePackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class SalonServiceService {

    private final SalonServiceRepository serviceRepo;
    private final AppointmentRepository appointmentRepo;
    private final ReviewRepository reviewRepo;
    private final ServicePackageRepository packageRepo;

    public SalonServiceService(SalonServiceRepository serviceRepo,
                               AppointmentRepository appointmentRepo,
                               ReviewRepository reviewRepo,
                               ServicePackageRepository packageRepo) {
        this.serviceRepo = serviceRepo;
        this.appointmentRepo = appointmentRepo;
        this.reviewRepo = reviewRepo;
        this.packageRepo = packageRepo;
    }

    public SalonService create(String name, String description, double price, int durationMinutes, String category, String imageUrl) {
        SalonService s = new SalonService(name, description, price, durationMinutes, category);
        if (imageUrl != null && !imageUrl.isBlank()) {
            s.setImageUrl(imageUrl);
        }
        return serviceRepo.save(s);
    }


    public List<SalonService> findAll() {
        return serviceRepo.findAll();
    }
    public Page<SalonService> findAll(Pageable pageable) {
        return serviceRepo.findAll(pageable);
    }

    public List<SalonService> findActive() {
        return serviceRepo.findByActive(true);
    }
    public Page<SalonService> findActive(Pageable pageable) {
        return serviceRepo.findByActive(true, pageable);
    }

    public Optional<SalonService> findById(Long id) {
        return serviceRepo.findById(id);
    }

    public List<SalonService> searchByName(String keyword) {
        return serviceRepo.findByNameContainingIgnoreCase(keyword);
    }
    public Page<SalonService> searchByName(String keyword, Pageable pageable) {
        return serviceRepo.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public Page<SalonService> findByNameAndCategory(String name, String category, Pageable pageable) {
        String nameFilter = (name != null && !name.isBlank()) ? name : null;
        String categoryFilter = (category != null && !category.isBlank()) ? category : null;
        return serviceRepo.findByNameAndCategory(nameFilter, categoryFilter, pageable);
    }

    public SalonService update(Long id, String name, String description, double price, int durationMinutes, String category, String imageUrl) {
        SalonService s = serviceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        if (name != null && !name.isBlank()) s.setName(name);
        if (description != null) s.setDescription(description);
        s.setPrice(price);
        s.setDurationMinutes(durationMinutes);
        if (category != null) s.setCategory(category);
        if (imageUrl != null) s.setImageUrl(imageUrl.isBlank() ? null : imageUrl);
        return serviceRepo.save(s);
    }

    public void toggleActive(Long id) {
        SalonService s = serviceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        s.setActive(!s.isActive());
        serviceRepo.save(s);
    }

    /**
     * Delete a service. Throws an error if there are existing appointments.
     * Reviews are preserved with nullified service reference.
     */
    @Transactional
    public void delete(Long id) {
        SalonService service = serviceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        // Check if there are any appointments for this service
        if (!appointmentRepo.findByServiceId(id).isEmpty()) {
            throw new RuntimeException("Cannot delete service: there are existing appointments associated with it.");
        }

        // 1. Nullify service reference in reviews to preserve historical data
        reviewRepo.findByServiceId(id).forEach(review -> {
            review.setService(null);
            reviewRepo.save(review);
        });

        // 2. Remove this service from any packages (ManyToMany join table)
        List<ServicePackage> packages = packageRepo.findAll();
        for (ServicePackage pkg : packages) {
            if (pkg.getServices().remove(service)) {
                packageRepo.save(pkg);
            }
        }

        // 3. Delete the service itself
        serviceRepo.delete(service);
    }
}

