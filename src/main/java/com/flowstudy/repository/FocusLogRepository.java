package com.flowstudy.repository;

import com.flowstudy.model.FocusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface FocusLogRepository extends JpaRepository<FocusLog, String> {

    // 統計需求 1：特定區間內，各科目的總專注時數 (用於圓餅圖)
    // 這裡利用我們建立的 idx_focuslog_user_time 索引，查詢會非常快
    @Query("SELECT f.subject.name as subjectName, SUM(f.durationMs) as totalDuration " +
           "FROM FocusLog f WHERE f.userId = :userId " +
           "AND f.endTime BETWEEN :startDate AND :endDate " +
           "GROUP BY f.subject.name")
    List<SubjectTimeAggregation> sumDurationBySubject(
            @Param("userId") String userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);

    // 統計需求 2：過去一年每日專注時數 (用於 GitHub 式熱點圖)
    // 使用 FUNCTION 呼叫資料庫底層的 DATE 轉換函數進行分組
    @Query("SELECT FUNCTION('DATE', f.endTime) as focusDate, SUM(f.durationMs) as dailyTotal " +
           "FROM FocusLog f WHERE f.userId = :userId " +
           "AND f.endTime >= :oneYearAgo " +
           "GROUP BY FUNCTION('DATE', f.endTime)")
    List<DailyFocusAggregation> getDailyFocusHeatmap(
            @Param("userId") String userId, 
            @Param("oneYearAgo") LocalDateTime oneYearAgo);

    // Spring Data Projections (介面映射)
    interface SubjectTimeAggregation {
        String getSubjectName();
        Long getTotalDuration();
    }
    
    interface DailyFocusAggregation {
        java.sql.Date getFocusDate();
        Long getDailyTotal();
    }
}