package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // ---- Dashboard counts ----
    long countByRole(String role);

    long countByEnabled(boolean enabled);

    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    // ---- Search / filter ----
    List<User> findByRole(String role);
    Page<User> findByRole(String role, Pageable pageable);

    List<User> findByUsernameContainingIgnoreCase(String keyword);
    Page<User> findByUsernameContainingIgnoreCase(String keyword, Pageable pageable);

    // ---- Reports: registrations per day ----
    @Query("SELECT CAST(u.createdAt AS date) AS regDate, COUNT(u) AS cnt " +
           "FROM User u WHERE u.createdAt BETWEEN :from AND :to " +
           "GROUP BY CAST(u.createdAt AS date) ORDER BY regDate")
    List<Object[]> countRegistrationsPerDay(@Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);

    // ---- Reports: users grouped by role ----
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countByRoleGrouped();
}