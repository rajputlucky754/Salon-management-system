package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByServiceIdAndVisible(Long serviceId, boolean visible);

    List<Review> findByStaffIdAndVisible(Long staffId, boolean visible);

    List<Review> findByCustomerId(Long customerId);

    Optional<Review> findByAppointmentId(Long appointmentId);

    boolean existsByAppointmentId(Long appointmentId);

    List<Review> findByVisible(boolean visible);

    List<Review> findTop6ByVisibleOrderByCreatedAtDesc(boolean visible);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.service.id = :serviceId AND r.visible = true")
    Double averageRatingByServiceId(@Param("serviceId") Long serviceId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.staff.id = :staffId AND r.visible = true")
    Double averageRatingByStaffId(@Param("staffId") Long staffId);

    List<Review> findByServiceId(Long serviceId);

    List<Review> findByStaffId(Long staffId);
}
