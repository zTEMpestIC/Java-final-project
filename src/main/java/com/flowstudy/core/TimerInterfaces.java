package com.flowstudy.core;

/**
 * 計時器狀態機回調介面
 * 由前端實現此介面，以響應計時器的狀態變化
 */
public interface ITimerCallback {
    /**
     * 計時器 Tick 事件 - 每 100ms 觸發一次
     * @param elapsedMs 已消耗時間（毫秒）
     * @param totalMs 本循環的總時間（毫秒）
     */
    void onTick(long elapsedMs, long totalMs);

    /**
     * 計時器暫停事件
     */
    void onPause();

    /**
     * 計時器繼續事件
     */
    void onResume();

    /**
     * 單個循環完成事件
     * @param isBreak 當前完成的循環是否為休息階段
     * @param nextPhaseMs 下一階段的時間（毫秒），若為 -1 表示全部番茄鐘完成
     */
    void onPhaseComplete(boolean isBreak, long nextPhaseMs);

    /**
     * 全部番茄鐘循環完成事件
     */
    void onAllCyclesComplete();
}

/**
 * 番茄鐘配置：支援自定義結構
 * 例如：50 分鐘專注 + 10 分鐘休息 × 3 循環
 */
public record PomodoroConfig(
    long focusMinutes,      // 專注時間（分鐘）
    long breakMinutes,      // 休息時間（分鐘）
    int cycles              // 循環數
) {
    public long getTotalTimeMs() {
        return (focusMinutes + breakMinutes) * cycles * 60_000L;
    }

    public static PomodoroConfig standard() {
        return new PomodoroConfig(25, 5, 4);
    }
}

/**
 * 計時器工作模式
 */
public enum TimerMode {
    FORWARD,    // 正計時
    BACKWARD,   // 倒計時
    POMODORO    // 番茄鐘循環
}

/**
 * 計時器狀態
 */
public enum TimerState {
    IDLE, RUNNING, PAUSED, COMPLETED, CANCELLED
}
