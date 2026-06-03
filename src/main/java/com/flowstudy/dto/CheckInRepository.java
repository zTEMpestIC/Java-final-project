package com.flowstudy.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.flowstudy.core.CheckIn;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {
    java.util.List<CheckIn> findByUserId(UUID userId);

    @Query("SELECT ci FROM CheckIn ci WHERE ci.userId = :userId ORDER BY ci.checkedInAt DESC LIMIT 1")
    Optional<CheckIn> findLatestCheckInByUserId(@Param("userId") UUID userId);

    @Query("SELECT ci FROM CheckIn ci WHERE ci.userId = :userId AND ci.checkedInAt >= :startDate AND ci.checkedInAt < :endDate")
    java.util.List<CheckIn> findCheckInsInDateRange(@Param("userId") UUID userId, 
                                                    @Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
}
