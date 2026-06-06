package com.flowstudy.repository;

import com.flowstudy.model.FocusLog;
import com.flowstudy.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class FocusLogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FocusLogRepository focusLogRepository;

    private String testUserId = "user_123";
    private Subject math;
    private Subject english;

    @BeforeEach
    void setUp() {
        // 1. 建立測試科目
        math = Subject.builder().id(UUID.randomUUID().toString()).name("數學").colorCode("#FF0000").build();
        english = Subject.builder().id(UUID.randomUUID().toString()).name("英文").colorCode("#0000FF").build();
        entityManager.persist(math);
        entityManager.persist(english);

        // 2. 建立測試專注紀錄 (模擬不同時間點完成的番茄鐘)
        LocalDateTime today = LocalDateTime.now();
        
        // 今天數學讀了 25 分鐘 (1500000 ms) + 25 分鐘
        createLog(math, 1500000L, today.minusHours(1));
        createLog(math, 1500000L, today.minusHours(2));
        
        // 今天英文讀了 30 分鐘 (1800000 ms)
        createLog(english, 1800000L, today.minusHours(3));
        
        // 昨天數學讀了 25 分鐘
        createLog(math, 1500000L, today.minusDays(1));
        
        entityManager.flush();
    }

    private void createLog(Subject subject, long durationMs, LocalDateTime endTime) {
        FocusLog log = FocusLog.builder()
                .userId(testUserId)
                .subject(subject)
                .durationMs(durationMs)
                .endTime(endTime)
                .build();
        entityManager.persist(log);
    }

    @Test
    void testSumDurationBySubject() {
        // 測試目標：撈取「今天」各科目的總時間
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59);

        List<FocusLogRepository.SubjectTimeAggregation> results = 
            focusLogRepository.sumDurationBySubject(testUserId, startOfDay, endOfDay);

        // 預期：數學 50分鐘(3000000ms), 英文 30分鐘(1800000ms)
        assertEquals(2, results.size(), "今天應該有兩個科目的紀錄");
        
        for (FocusLogRepository.SubjectTimeAggregation result : results) {
            if (result.getSubjectName().equals("數學")) {
                assertEquals(3000000L, result.getTotalDuration());
            } else if (result.getSubjectName().equals("英文")) {
                assertEquals(1800000L, result.getTotalDuration());
            }
        }
    }

    @Test
    void testGetDailyFocusHeatmap() {
        // 測試目標：撈取過去一年的每日總時數分佈
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        
        List<FocusLogRepository.DailyFocusAggregation> results = 
            focusLogRepository.getDailyFocusHeatmap(testUserId, oneYearAgo);

        // 預期：有兩天的紀錄（今天、昨天）
        assertEquals(2, results.size(), "應該要有兩天的熱點數據");
        
        // 可以看到終端機會印出 H2 的 GROUP BY 語句，證明聚合邏輯是在 DB 層高效運算的
    }
}