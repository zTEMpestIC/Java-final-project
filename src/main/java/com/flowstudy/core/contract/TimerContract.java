package com.flowstudy.core.contract;

public interface TimerContract {

    // --- Enums ---
    enum Mode { 
        FORWARD, BACKWARD, POMODORO 
    }
    
    enum State { 
        STOPPED, RUNNING, PAUSED 
    }

    // --- Interfaces ---
    /**
     * 計時器回調介面，前端 UI 或後端 WebSocket 可實作此介面來接收實時狀態
     */
    interface ITimerCallback {
        void onTick(long remainingMs, long elapsedMs);
        void onStateChanged(State oldState, State newState);
        void onComplete(Mode mode);
    }

    // --- DTOs ---
    record TimerConfigDTO(Mode mode, long durationMs) {
        // 緊湊建構子：自動校驗傳入參數 (Java 21 特性)
        public TimerConfigDTO {
            if (durationMs < 0) {
                throw new IllegalArgumentException("時長不能為負數 (Duration cannot be negative)");
            }
        }

        // 靜態工廠方法：提供常用的番茄鐘預設配置
        public static TimerConfigDTO defaultPomodoro() {
            return new TimerConfigDTO(Mode.POMODORO, 25 * 60 * 1000L); // 25分鐘
        }
    }
}