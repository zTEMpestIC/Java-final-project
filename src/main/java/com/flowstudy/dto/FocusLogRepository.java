package com.flowstudy.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.flowstudy.core.FocusLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FocusLogRepository extends JpaRepository<FocusLog, UUID> {
    List<FocusLog> findByUserId(UUID userId);

    List<FocusLog> findBySubjectId(UUID subjectId);

    @Query("SELECT fl FROM FocusLog fl WHERE fl.userId = :userId AND fl.createdAt BETWEEN :startDate AND :endDate")
    List<FocusLog> findByUserIdAndDateRange(@Param("userId") UUID userId, 
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(fl.durationMs) FROM FocusLog fl WHERE fl.subjectId = :subjectId")
    long getTotalFocusTimeBySubject(@Param("subjectId") UUID subjectId);

    @Query("SELECT SUM(fl.durationMs) FROM FocusLog fl WHERE fl.userId = :userId")
    long getTotalFocusTimeByUser(@Param("userId") UUID userId);
}
