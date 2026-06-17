package com.flowstudy.core;

import com.flowstudy.core.contract.MilestoneAndSocialContract.MilestoneDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MilestoneSchedulerTest {

    @Test
    void testCalculateDailyTargets_NormalEfficiency() {
        MilestoneScheduler scheduler = new MilestoneScheduler();
        
        // Arrange: 設定 4 天後的截止日
        LocalDate deadline = LocalDate.now().plusDays(4);
        
        // 總進度 100，目前 20，剩 4 天，效率 1.0 (正常)
        MilestoneDTO dto = new MilestoneDTO(
            "M1", "讀完 Spring Boot 實戰", 100, 20, deadline, 1.0
        );
        
        // Act
        List<Integer> targets = scheduler.calculateDailyTargets(dto);
        
        // Assert: (100 - 20) / 4 = 20 頁/天
        assertEquals(4, targets.size(), "應該要分配 4 天的任務");
        assertEquals(20, targets.get(0), "每天應該要讀 20 頁"); 
    }

    @Test
    void testCalculateDailyTargets_LowEfficiencyAdjustsUpward() {
        MilestoneScheduler scheduler = new MilestoneScheduler();
        LocalDate deadline = LocalDate.now().plusDays(4);
        
        // 總進度 100，目前 20，剩 4 天，效率 0.5 (代表過去進度嚴重落後)
        MilestoneDTO dto = new MilestoneDTO(
            "M2", "補回落後進度", 100, 20, deadline, 0.5
        );
        
        List<Integer> targets = scheduler.calculateDailyTargets(dto);
        
        // Assert: 正常是 20，但因為效率低 (2.0 - 0.5 = 1.5倍的壓力)，所以 20 * 1.5 = 30 頁/天
        assertEquals(30, targets.get(0), "因為過去效率低，AI 應自動提高每日目標以避免死線爆炸");
    }
}
