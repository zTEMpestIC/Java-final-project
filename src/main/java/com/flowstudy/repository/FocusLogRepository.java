package com.flowstudy.repository;

import com.flowstudy.model.FocusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public interface FocusLogRepository extends JpaRepository<FocusLog, String> {

    // 統計需求 1：特定區間內，各科目的總專注時數 (用於圓餅圖)
    @Query("SELECT f.subject.name as subjectName, SUM(f.durationMs) as totalDuration " +
           "FROM FocusLog f WHERE f.userId = :userId " +
           "AND f.endTime BETWEEN :startDate AND :endDate " +
           "GROUP BY f.subject.name")
    List<SubjectTimeAggregation> sumDurationBySubject(
            @Param("userId") String userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);

    // 🌟 統計需求 2：使用標準 JPA CAST 語法，完美相容 H2 與 PostgreSQL
    @Query("SELECT cast(f.endTime as date) as focusDate, SUM(f.durationMs) as dailyTotal " +
           "FROM FocusLog f WHERE f.userId = :userId " +
           "AND f.endTime >= :oneYearAgo " +
           "GROUP BY cast(f.endTime as date)")
    List<DailyFocusAggregation> getDailyFocusHeatmap(
            @Param("userId") String userId, 
            @Param("oneYearAgo") LocalDateTime oneYearAgo);

    // Spring Data Projections (介面映射)
    interface SubjectTimeAggregation {
        String getSubjectName();
        Long getTotalDuration();
    }
    
    interface DailyFocusAggregation {
        LocalDate getFocusDate(); // 升級為更現代的 java.time.LocalDate
        Long getDailyTotal();
    }
}