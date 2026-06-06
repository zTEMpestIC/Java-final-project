package com.flowstudy.core;

import java.util.ArrayList;
import java.util.List;

public class MilestoneScheduler {
    
    public List<Integer> calculateDailyTargets(MilestoneDTO dto) {
        List<Integer> dailyTargets = new ArrayList<>();
        int remainingWork = dto.totalTarget() - dto.currentProgress();
        
        if (remainingWork <= 0 || dto.daysRemaining() <= 0) {
            return dailyTargets;
        }

        // Apply efficiency modifier. If efficiency is < 1.0, required daily target goes up.
        double efficiencyModifier = 2.0 - dto.historicalEfficiency();
        double baseDailyTarget = ((double) remainingWork / dto.daysRemaining()) * efficiencyModifier;
        int adjustedTarget = (int) Math.ceil(Math.max(1.0, baseDailyTarget));

        for (int i = 0; i < dto.daysRemaining(); i++) {
            dailyTargets.add(adjustedTarget);
        }
        
        return dailyTargets;
    }
}