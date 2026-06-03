package com.flowstudy.core;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MilestoneScheduler 單元測試
 */
public class MilestoneSchedulerTest {

    @Test
    void testGenerateBasicSchedule() {
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(10);
        MilestoneScheduler scheduler = new MilestoneScheduler(deadline, 100.0);

        List<MilestoneScheduler.DailyMilestoneTarget> schedule =
            scheduler.generateMilestoneSchedule();

        // 應該有 11 天（包括今天和截止日）
        assertEquals(11, schedule.size());

        // 驗證累計目標在最後一天達到 100
        double lastCumulative = schedule.get(schedule.size() - 1).cumulativeTarget();
        assertEquals(100.0, lastCumulative, 0.1);

        // 驗證每日目標都是正數
        for (MilestoneScheduler.DailyMilestoneTarget target : schedule) {
            assertTrue(target.dailyTarget() > 0);
        }
    }

    @Test
    void testScheduleWithHistoricalEfficiency() {
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(10);

        // 模擬歷史效率 - 70% 完成率
        List<MilestoneScheduler.HistoricalEfficiency> history = new ArrayList<>();
        history.add(new MilestoneScheduler.HistoricalEfficiency(
            today.minusDays(1), 10.0, 7.0, 0.7
        ));

        MilestoneScheduler scheduler = new MilestoneScheduler(deadline, 100.0, history);
        assertEquals(1.0, scheduler.getCompletionEfficiency()); // 低於 70% 的話會被調高

        double riskLevel = scheduler.calculateRiskLevel();
        assertTrue(riskLevel > 0.2); // 應該有一定風險
    }

    @Test
    void testDeadlineValidation() {
        LocalDate today = LocalDate.now();
        LocalDate pastDate = today.minusDays(1);

        assertThrows(IllegalArgumentException.class, () ->
            new MilestoneScheduler(pastDate, 100.0)
        );
    }

    @Test
    void testNegativeProgressValidation() {
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(10);

        assertThrows(IllegalArgumentException.class, () ->
            new MilestoneScheduler(deadline, -50.0)
        );
    }

    @Test
    void testRecalculateSchedule() {
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(10);
        MilestoneScheduler scheduler = new MilestoneScheduler(deadline, 100.0);

        Map<LocalDate, Double> actualProgress = new HashMap<>();
        actualProgress.put(today, 15.0);
        actualProgress.put(today.plusDays(1), 12.0);

        List<MilestoneScheduler.DailyMilestoneTarget> newSchedule =
            scheduler.recalculateSchedule(actualProgress);

        // 應該重新計算後續計畫
        assertNotNull(newSchedule);
        assertTrue(newSchedule.size() > 0);
    }

    @Test
    void testWarningGeneration() {
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(5);

        List<MilestoneScheduler.HistoricalEfficiency> history = new ArrayList<>();
        // 模擬完成率低於 30%
        for (int i = 0; i < 7; i++) {
            history.add(new MilestoneScheduler.HistoricalEfficiency(
                today.minusDays(i), 10.0, 2.0, 0.2
            ));
        }

        MilestoneScheduler scheduler = new MilestoneScheduler(deadline, 100.0, history);
        String warning = scheduler.generateWarning();

        assertTrue(warning.contains("進度嚴重落後"));
    }
}
