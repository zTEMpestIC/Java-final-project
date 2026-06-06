package com.flowstudy.core;

// 1. 引入新路徑下的 MilestoneDTO
import com.flowstudy.core.contract.MilestoneAndSocialContract.MilestoneDTO;

import java.util.ArrayList;
import java.util.List;

public class MilestoneScheduler {
    
    public List<Integer> calculateDailyTargets(MilestoneDTO dto) {
        List<Integer> dailyTargets = new ArrayList<>();
        int remainingWork = dto.totalTarget() - dto.currentProgress();
        
        // 2. 使用 DTO 內建的方法來動態計算剩餘天數
        int daysRemaining = dto.getDaysRemaining();
        
        if (remainingWork <= 0 || daysRemaining <= 0) {
            return dailyTargets;
        }

        // 套用效率權重：效率越低（< 1.0），代表過去常落後，每日所需目標會提高以防做不完
        double efficiencyModifier = 2.0 - dto.historicalEfficiency();
        double baseDailyTarget = ((double) remainingWork / daysRemaining) * efficiencyModifier;
        int adjustedTarget = (int) Math.ceil(Math.max(1.0, baseDailyTarget));

        for (int i = 0; i < daysRemaining; i++) {
            dailyTargets.add(adjustedTarget);
        }
        
        return dailyTargets;
    }
}