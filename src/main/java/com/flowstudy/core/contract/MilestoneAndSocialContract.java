package com.flowstudy.core.contract;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface MilestoneAndSocialContract {

    // 里程碑 DTO (保留給 MilestoneScheduler 使用)
    record MilestoneDTO(
        String id, 
        String title, 
        int totalTarget, 
        int currentProgress, 
        LocalDate deadline, 
        double historicalEfficiency
    ) {
        public MilestoneDTO {
            if (historicalEfficiency <= 0) {
                throw new IllegalArgumentException("歷史效率必須大於 0 (Efficiency must be > 0)");
            }
            if (currentProgress > totalTarget) {
                currentProgress = totalTarget; // 自動校正防呆
            }
        }

        // 計算剩餘天數的輔助方法
        public int getDaysRemaining() {
            long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deadline);
            return Math.max(0, (int) days);
        }
    }

    // 自習室打卡 DTO (準備給 Phase 3 WebSocket 使用)
    record CheckInDTO(String userId, String roomId, LocalDateTime checkInTime, String statusMessage) {
        public static CheckInDTO checkInNow(String userId, String roomId, String statusMessage) {
            return new CheckInDTO(userId, roomId, LocalDateTime.now(), statusMessage);
        }
    }
}