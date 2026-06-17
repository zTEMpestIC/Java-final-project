package com.flowstudy.core;

import com.flowstudy.core.contract.TimerContract;
import com.flowstudy.core.contract.TimerContract.State;
import com.flowstudy.core.contract.TimerContract.Mode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerStateMachine {
    private State state = State.STOPPED;
    private Mode mode;
    private long timeRemainingMs;
    private long timeElapsedMs;
    private final TimerContract.ITimerCallback callback;
    private ScheduledExecutorService scheduler;

    public TimerStateMachine(TimerContract.ITimerCallback callback) {
        this.callback = callback;
    }

    // 使用 DTO 作為參數，享受 record 帶來的自動防呆校驗
    public synchronized void start(TimerContract.TimerConfigDTO config) {
        if (state == State.RUNNING) return;
        
        this.mode = config.mode();
        this.timeRemainingMs = config.durationMs();
        this.timeElapsedMs = 0;
        
        changeState(State.RUNNING);
        startScheduler();
    }

    public synchronized void pause() {
        if (state == State.RUNNING) {
            changeState(State.PAUSED);
            stopScheduler();
        }
    }

    public synchronized void resume() {
        if (state == State.PAUSED) {
            changeState(State.RUNNING);
            startScheduler();
        }
    }

    // 由於使用者主動中斷
    public synchronized void stop() {
        if (state == State.STOPPED) return;
        
        changeState(State.STOPPED);
        stopScheduler();
        timeRemainingMs = 0;
        timeElapsedMs = 0;
    }

    // 內部方法：由於時間到而自然完成
    private synchronized void complete() {
        changeState(State.STOPPED);
        stopScheduler();
        if (callback != null) {
            // 確保最後一次的 UI 更新歸零
            callback.onTick(0, timeElapsedMs);
            callback.onComplete(this.mode);
        }
        timeRemainingMs = 0;
        timeElapsedMs = 0;
    }

    // 統一的狀態變更處理中心，負責觸發回調
    private void changeState(State newState) {
        if (this.state != newState) {
            State oldState = this.state;
            this.state = newState;
            if (callback != null) {
                callback.onStateChanged(oldState, newState);
            }
        }
    }

    private void startScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        // 每 100 毫秒執行一次 tick
        scheduler.scheduleAtFixedRate(this::tick, 100, 100, TimeUnit.MILLISECONDS);
    }

    private void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    private synchronized void tick() {
        if (state != State.RUNNING) return;

        timeElapsedMs += 100;
        
        if (mode == Mode.BACKWARD || mode == Mode.POMODORO) {
            timeRemainingMs -= 100;
            if (timeRemainingMs <= 0) {
                timeRemainingMs = 0;
                complete();
                return;
            }
        } else if (mode == Mode.FORWARD) {
            // 正計時沒有剩餘時間的概念，將其與經過時間同步（或保持為 0，視前端需求）
            timeRemainingMs += 100; 
        }
        
        if (callback != null) {
            callback.onTick(timeRemainingMs, timeElapsedMs);
        }
    }
}
