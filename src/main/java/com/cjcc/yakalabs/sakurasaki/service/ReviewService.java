package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.*;
import com.cjcc.yakalabs.sakurasaki.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final AppointmentRepository appointmentRepo;
    private final CustomerRepository customerRepo;

    public ReviewService(ReviewRepository reviewRepo,
                         AppointmentRepository appointmentRepo,
                         CustomerRepository customerRepo) {
        this.reviewRepo = reviewRepo;
        this.appointmentRepo = appointmentRepo;
        this.customerRepo = customerRepo;
    }

    /**
     * Submit a review — only allowed for COMPLETED appointments, and only one review per appointment.
     */
    public Review submitReview(Long appointmentId, int rating, String comment) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!"COMPLETED".equals(appointment.getStatus())) {
            throw new RuntimeException("You can only review completed appointments.");
        }

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5.");
        }

        Optional<Review> existingOpt = reviewRepo.findByAppointmentId(appointmentId);
        if (existingOpt.isPresent()) {
            Review existing = existingOpt.get();
            existing.setRating(rating);
            existing.setComment(comment);
            return reviewRepo.save(existing);
        }

        Review review = new Review(
                appointment.getCustomer(),
                appointment.getService(),
                appointment.getStaff(),
                appointment,
                rating,
                comment
        );
        return reviewRepo.save(review);
    }

    public List<Review> getReviewsForService(Long serviceId) {
        return reviewRepo.findByServiceIdAndVisible(serviceId, true);
    }

    public List<Review> getReviewsForStaff(Long staffId) {
        return reviewRepo.findByStaffIdAndVisible(staffId, true);
    }

    public List<Review> getReviewsByCustomer(Long customerId) {
        return reviewRepo.findByCustomerId(customerId);
    }

    public List<Review> getAllReviews() {
        return reviewRepo.findAll();
    }

    public Optional<Review> findById(Long id) {
        return reviewRepo.findById(id);
    }

    public Optional<Review> findByAppointmentId(Long appointmentId) {
        return reviewRepo.findByAppointmentId(appointmentId);
    }

    public Double getAverageRatingForService(Long serviceId) {
        return reviewRepo.averageRatingByServiceId(serviceId);
    }

    public Double getAverageRatingForStaff(Long staffId) {
        return reviewRepo.averageRatingByStaffId(staffId);
    }

    /**
     * Edit a review (only by the original author).
     */
    public Review editReview(Long reviewId, Long customerId, int rating, String comment) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("You can only edit your own reviews.");
        }

        review.setRating(rating);
        review.setComment(comment);
        return reviewRepo.save(review);
    }

    /**
     * Delete a review (by author or admin).
     */
    public void deleteReview(Long reviewId) {
        reviewRepo.deleteById(reviewId);
    }

    /**
     * Admin moderation: toggle visibility.
     */
    public void toggleVisibility(Long reviewId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setVisible(!review.isVisible());
        reviewRepo.save(review);
    }
}
