package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {



    List<SalonService> findByActive(boolean active);
    Page<SalonService> findByActive(boolean active, Pageable pageable);

    List<SalonService> findByNameContainingIgnoreCase(String keyword);
    Page<SalonService> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT s FROM SalonService s WHERE " +
           "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:category IS NULL OR LOWER(s.category) = LOWER(:category))")
    Page<SalonService> findByNameAndCategory(@Param("name") String name, @Param("category") String category, Pageable pageable);
}
