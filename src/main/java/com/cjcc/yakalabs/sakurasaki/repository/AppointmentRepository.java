package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Count appointments on a specific date
    long countByAppointmentDate(LocalDate date);

    // Find all appointments on a specific date
    List<Appointment> findByAppointmentDate(LocalDate date);
    Page<Appointment> findByAppointmentDate(LocalDate date, Pageable pageable);

    // Find appointments by status
    List<Appointment> findByStatus(String status);
    Page<Appointment> findByStatus(String status, Pageable pageable);

    // Find appointments for a specific customer (ordered by date desc)
    List<Appointment> findByCustomerIdOrderByAppointmentDateDesc(Long customerId);
    Page<Appointment> findByCustomerIdOrderByAppointmentDateDesc(Long customerId, Pageable pageable);

    // Find appointments by status for a specific customer (ordered by date desc)
    Page<Appointment> findByCustomerIdAndStatusOrderByAppointmentDateDesc(Long customerId, String status, Pageable pageable);
    Page<Appointment> findByCustomerIdAndStatusNotOrderByAppointmentDateDesc(Long customerId, String status, Pageable pageable);

    // Find appointments for a specific customer
    List<Appointment> findByCustomerId(Long customerId);

    // Find appointments for a specific staff member
    List<Appointment> findByStaffId(Long staffId);
    Page<Appointment> findByStaffId(Long staffId, Pageable pageable);

    // Find appointments for a staff member on a specific date
    List<Appointment> findByStaffIdAndAppointmentDate(Long staffId, LocalDate date);

    // ---- Double-booking detection ----
    @Query("SELECT a FROM Appointment a WHERE a.staff.id = :staffId " +
           "AND a.appointmentDate = :date " +
           "AND a.status != 'CANCELLED' " +
           "AND a.appointmentTime < :endTime " +
           "AND :startTime < FUNCTION('ADDTIME', a.appointmentTime, FUNCTION('SEC_TO_TIME', a.service.durationMinutes * 60))")
    List<Appointment> findConflictingAppointments(@Param("staffId") Long staffId,
                                                  @Param("date") LocalDate date,
                                                  @Param("startTime") LocalTime startTime,
                                                  @Param("endTime") LocalTime endTime);

    // ---- Report: appointments per day within a date range ----
    @Query("SELECT a.appointmentDate, COUNT(a) " +
           "FROM Appointment a " +
           "WHERE a.appointmentDate BETWEEN :from AND :to " +
           "GROUP BY a.appointmentDate " +
           "ORDER BY a.appointmentDate")
    List<Object[]> countAppointmentsPerDay(@Param("from") LocalDate from,
                                           @Param("to") LocalDate to);

    // ---- Report: most booked services within a date range ----
    @Query("SELECT a.service.name, COUNT(a) " +
           "FROM Appointment a " +
           "WHERE a.appointmentDate BETWEEN :from AND :to " +
           "GROUP BY a.service.name " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> countByServiceBetween(@Param("from") LocalDate from,
                                         @Param("to") LocalDate to);

    // ---- Revenue: sum of service prices for completed appointments ----
    @Query("SELECT COALESCE(SUM(a.service.price), 0) FROM Appointment a WHERE a.status = 'COMPLETED'")
    Double sumRevenueCompleted();

    // ---- Latest appointments for dashboard display ----
    List<Appointment> findTop5ByOrderByAppointmentDateDescAppointmentTimeDesc();

    // ---- Find next upcoming appointment for a customer ----
    Optional<Appointment> findFirstByCustomerIdAndAppointmentDateGreaterThanEqualAndStatusOrderByAppointmentDateAscAppointmentTimeAsc(
            Long customerId, LocalDate date, String status);

    // ---- Count completed appointments for a customer ----
    long countByCustomerIdAndStatus(Long customerId, String status);

    // ---- Find appointments for a specific service ----
    List<Appointment> findByServiceId(Long serviceId);
}
