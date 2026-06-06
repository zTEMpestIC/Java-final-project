package com.flowstudy.core;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MilestoneSchedulerTest {

    @Test
    void testCalculateDailyTargets() {
        MilestoneScheduler scheduler = new MilestoneScheduler();
        // 100 total pages, 20 read, 4 days left, 1.0 (100%) efficiency
        MilestoneDTO dto = new MilestoneDTO(100, 20, 4, 1.0);
        
        List<Integer> targets = scheduler.calculateDailyTargets(dto);
        
        assertEquals(4, targets.size());
        assertEquals(20, targets.get(0)); // (100-20)/4 = 20 per day
    }
}