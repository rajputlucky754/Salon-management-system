package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import com.cjcc.yakalabs.sakurasaki.model.ServicePackage;
import com.cjcc.yakalabs.sakurasaki.repository.SalonServiceRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ServicePackageRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServicePackageService {

    private final ServicePackageRepository packageRepo;
    private final SalonServiceRepository serviceRepo;

    public ServicePackageService(ServicePackageRepository packageRepo, SalonServiceRepository serviceRepo) {
        this.packageRepo = packageRepo;
        this.serviceRepo = serviceRepo;
    }

    public ServicePackage create(String name, String description, double discountPercent, List<Long> serviceIds) {
        ServicePackage pkg = new ServicePackage(name, description, discountPercent);
        List<SalonService> services = serviceRepo.findAllById(serviceIds);
        pkg.setServices(new HashSet<>(services));
        return packageRepo.save(pkg);
    }

    public List<ServicePackage> findAll() {
        return packageRepo.findAll();
    }

    public List<ServicePackage> findActive() {
        return packageRepo.findByActive(true);
    }

    public Optional<ServicePackage> findById(Long id) {
        return packageRepo.findById(id);
    }

    public ServicePackage update(Long id, String name, String description, double discountPercent, List<Long> serviceIds) {
        ServicePackage pkg = packageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
        if (name != null && !name.isBlank()) pkg.setName(name);
        if (description != null) pkg.setDescription(description);
        pkg.setDiscountPercent(discountPercent);
        if (serviceIds != null) {
            pkg.setServices(new HashSet<>(serviceRepo.findAllById(serviceIds)));
        }
        return packageRepo.save(pkg);
    }

    public void toggleActive(Long id) {
        ServicePackage pkg = packageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
        pkg.setActive(!pkg.isActive());
        packageRepo.save(pkg);
    }

    public void delete(Long id) {
        packageRepo.deleteById(id);
    }
}
