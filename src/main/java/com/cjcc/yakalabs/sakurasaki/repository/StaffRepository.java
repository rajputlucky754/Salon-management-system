package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {


    List<Staff> findByActive(boolean active);
    Page<Staff> findByActive(boolean active, Pageable pageable);

    List<Staff> findBySpecializationContainingIgnoreCase(String specialization);
    Page<Staff> findBySpecializationContainingIgnoreCase(String specialization, Pageable pageable);

    List<Staff> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    Page<Staff> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);

    @Query("SELECT s FROM Staff s WHERE " +
           "(:name IS NULL OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:spec IS NULL OR LOWER(s.specialization) LIKE LOWER(CONCAT('%', :spec, '%')))")
    Page<Staff> findByNameAndSpecialization(@Param("name") String name, @Param("spec") String spec, Pageable pageable);
}
