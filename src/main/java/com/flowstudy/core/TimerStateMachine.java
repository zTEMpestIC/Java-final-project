package com.flowstudy.core;

import java.util.logging.Logger;

/**
 * 通用計時器核心引擎 - 支援正計時、倒計時、自定義番茄鐘循環
 */
public class TimerStateMachine {
    private static final Logger logger = Logger.getLogger(TimerStateMachine.class.getName());
    
    private final TimerMode mode;
    private final PomodoroConfig pomodoroConfig;
    private final long targetTimeMs;
    private final ITimerCallback callback;

    private TimerState state = TimerState.IDLE;
    private long elapsedMs = 0;
    private int currentCycleIndex = 0;
    private boolean isCurrentlyBreak = false;

    private volatile boolean isRunning = false;
    private Thread timerThread;

    // 建構式

    public TimerStateMachine(PomodoroConfig config, ITimerCallback callback) {
        this.mode = TimerMode.POMODORO;
        this.pomodoroConfig = config;
        this.targetTimeMs = -1;
        this.callback = callback;
    }

    public TimerStateMachine(TimerMode mode, long targetTimeMs, ITimerCallback callback) {
        if (mode == TimerMode.POMODORO) {
            throw new IllegalArgumentException("請使用 PomodoroConfig 建構子");
        }
        this.mode = mode;
        this.pomodoroConfig = null;
        this.targetTimeMs = targetTimeMs;
        this.callback = callback;
    }

    // 公開介面

    public synchronized void start() {
        if (state == TimerState.RUNNING || state == TimerState.COMPLETED) {
            return;
        }

        state = TimerState.RUNNING;
        isRunning = true;

        if (timerThread != null && timerThread.isAlive()) {
            return;
        }

        timerThread = new Thread(this::timerLoop);
        timerThread.setDaemon(true);
        timerThread.setName("TimerStateMachine-" + Thread.currentThread().getId());
        timerThread.start();

        callback.onResume();
    }

    public synchronized void pause() {
        if (state != TimerState.RUNNING) {
            return;
        }
        state = TimerState.PAUSED;
        isRunning = false;
        callback.onPause();
    }

    public synchronized void resume() {
        if (state != TimerState.PAUSED) {
            return;
        }
        state = TimerState.RUNNING;
        isRunning = true;
        callback.onResume();
    }

    public synchronized void stop() {
        state = TimerState.CANCELLED;
        isRunning = false;
        elapsedMs = 0;
        currentCycleIndex = 0;
        isCurrentlyBreak = false;
    }

    public TimerState getState() {
        return state;
    }

    public long getElapsedMs() {
        return elapsedMs;
    }

    public int getProgressPercentage() {
        if (mode == TimerMode.POMODORO) {
            long currentPhaseMs = isCurrentlyBreak ?
                pomodoroConfig.breakMinutes() * 60_000L :
                pomodoroConfig.focusMinutes() * 60_000L;
            return (int) ((elapsedMs % currentPhaseMs) * 100 / currentPhaseMs);
        } else {
            return (int) (elapsedMs * 100 / targetTimeMs);
        }
    }

    public int getCurrentCycleIndex() {
        return currentCycleIndex;
    }

    public boolean isCurrentlyBreak() {
        return isCurrentlyBreak;
    }

    // 私有方法

    private void timerLoop() {
        long lastUpdateMs = System.currentTimeMillis();

        while (isRunning) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (!isRunning) {
                break;
            }

            long currentTimeMs = System.currentTimeMillis();
            long deltaMs = currentTimeMs - lastUpdateMs;
            lastUpdateMs = currentTimeMs;

            updateTimer(deltaMs);
        }
    }

    private synchronized void updateTimer(long deltaMs) {
        if (state != TimerState.RUNNING) {
            return;
        }

        elapsedMs += deltaMs;

        switch (mode) {
            case FORWARD -> handleForwardMode();
            case BACKWARD -> handleBackwardMode();
            case POMODORO -> handlePomodoroMode();
        }
    }

    private void handleForwardMode() {
        callback.onTick(elapsedMs, targetTimeMs);
        if (elapsedMs >= targetTimeMs) {
            state = TimerState.COMPLETED;
            isRunning = false;
            callback.onAllCyclesComplete();
        }
    }

    private void handleBackwardMode() {
        long remainingMs = targetTimeMs - elapsedMs;
        callback.onTick(remainingMs, targetTimeMs);
        if (remainingMs <= 0) {
            state = TimerState.COMPLETED;
            isRunning = false;
            callback.onAllCyclesComplete();
        }
    }

    private void handlePomodoroMode() {
        long focusMs = pomodoroConfig.focusMinutes() * 60_000L;
        long breakMs = pomodoroConfig.breakMinutes() * 60_000L;
        long cycleMs = focusMs + breakMs;
        long totalMs = cycleMs * pomodoroConfig.cycles();

        if (elapsedMs >= totalMs) {
            state = TimerState.COMPLETED;
            isRunning = false;
            callback.onAllCyclesComplete();
            return;
        }

        long positionInCycle = elapsedMs % cycleMs;
        int newCycleIndex = (int) (elapsedMs / cycleMs);
        boolean newIsBreak = positionInCycle >= focusMs;

        if (newCycleIndex != currentCycleIndex || newIsBreak != isCurrentlyBreak) {
            currentCycleIndex = newCycleIndex;
            isCurrentlyBreak = newIsBreak;

            long remainingInTotal = totalMs - elapsedMs;
            callback.onPhaseComplete(!isCurrentlyBreak, remainingInTotal > 0 ? remainingInTotal : -1);
        }

        long currentPhaseMs = isCurrentlyBreak ? breakMs : focusMs;
        long elapsedInPhase = elapsedMs % cycleMs;
        if (isCurrentlyBreak) {
            elapsedInPhase -= focusMs;
        }

        callback.onTick(elapsedInPhase, currentPhaseMs);
    }
}
