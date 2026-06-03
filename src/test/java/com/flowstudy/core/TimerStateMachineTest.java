package com.flowstudy.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TimerStateMachine 單元測試
 */
public class TimerStateMachineTest {
    private TimerStateMachine timer;
    private MockTimerCallback callback;

    @BeforeEach
    void setUp() {
        callback = new MockTimerCallback();
    }

    @Test
    void testPomodoroModeStart() {
        PomodoroConfig config = new PomodoroConfig(1, 1, 1); // 1分鐘專注, 1分鐘休息
        timer = new TimerStateMachine(config, callback);

        assertEquals(TimerState.IDLE, timer.getState());

        timer.start();
        assertEquals(TimerState.RUNNING, timer.getState());
        assertTrue(callback.resumeCalled);
    }

    @Test
    void testPomodoroModePauseResume() throws InterruptedException {
        PomodoroConfig config = new PomodoroConfig(1, 1, 1);
        timer = new TimerStateMachine(config, callback);

        timer.start();
        Thread.sleep(200);

        timer.pause();
        assertEquals(TimerState.PAUSED, timer.getState());
        assertTrue(callback.pauseCalled);

        long elapsedBefore = timer.getElapsedMs();
        Thread.sleep(100);
        long elapsedAfter = timer.getElapsedMs();
        assertEquals(elapsedBefore, elapsedAfter); // 暫停時不應該繼續計時

        timer.resume();
        assertEquals(TimerState.RUNNING, timer.getState());
    }

    @Test
    void testForwardMode() throws InterruptedException {
        long targetMs = 500; // 500ms
        timer = new TimerStateMachine(TimerMode.FORWARD, targetMs, callback);

        timer.start();
        Thread.sleep(600); // 等待完成

        assertEquals(TimerState.COMPLETED, timer.getState());
        assertTrue(callback.allCyclesCompleteCalled);
        assertTrue(timer.getElapsedMs() >= targetMs);
    }

    @Test
    void testBackwardMode() throws InterruptedException {
        long targetMs = 500;
        timer = new TimerStateMachine(TimerMode.BACKWARD, targetMs, callback);

        timer.start();
        Thread.sleep(600);

        assertEquals(TimerState.COMPLETED, timer.getState());
    }

    @Test
    void testStop() throws InterruptedException {
        PomodoroConfig config = new PomodoroConfig(10, 10, 1);
        timer = new TimerStateMachine(config, callback);

        timer.start();
        Thread.sleep(200);

        timer.stop();
        assertEquals(TimerState.CANCELLED, timer.getState());
        assertEquals(0, timer.getElapsedMs());
    }

    @Test
    void testPomodoroConfigStandard() {
        PomodoroConfig config = PomodoroConfig.standard();
        assertEquals(25, config.focusMinutes());
        assertEquals(5, config.breakMinutes());
        assertEquals(4, config.cycles());
    }

    // Mock 實現
    private static class MockTimerCallback implements ITimerCallback {
        public boolean resumeCalled = false;
        public boolean pauseCalled = false;
        public boolean allCyclesCompleteCalled = false;
        public int tickCount = 0;

        @Override
        public void onTick(long elapsedMs, long totalMs) {
            tickCount++;
        }

        @Override
        public void onPause() {
            pauseCalled = true;
        }

        @Override
        public void onResume() {
            resumeCalled = true;
        }

        @Override
        public void onPhaseComplete(boolean isBreak, long nextPhaseMs) {
        }

        @Override
        public void onAllCyclesComplete() {
            allCyclesCompleteCalled = true;
        }
    }
}
