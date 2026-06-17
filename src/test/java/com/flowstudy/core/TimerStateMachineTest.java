package com.flowstudy.core;

import com.flowstudy.core.contract.TimerContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TimerStateMachineTest {

    private TestTimerCallback callback;
    private TimerStateMachine timer;

    // 自定義一個 Callback 用於收集測試數據
    static class TestTimerCallback implements TimerContract.ITimerCallback {
        public CountDownLatch completeLatch = new CountDownLatch(1);
        public List<TimerContract.State> stateHistory = new ArrayList<>();
        public boolean isCompleted = false;
        public long finalElapsedMs = 0;

        @Override
        public void onTick(long remainingMs, long elapsedMs) {
            this.finalElapsedMs = elapsedMs;
        }

        @Override
        public void onStateChanged(TimerContract.State oldState, TimerContract.State newState) {
            stateHistory.add(newState);
        }

        @Override
        public void onComplete(TimerContract.Mode mode) {
            isCompleted = true;
            completeLatch.countDown();
        }
    }

    @BeforeEach
    void setUp() {
        callback = new TestTimerCallback();
        timer = new TimerStateMachine(callback);
    }

    @Test
    void testTimerCompletion() throws InterruptedException {
        // Arrange: 建立一個 300 毫秒的倒計時
        TimerContract.TimerConfigDTO config = new TimerContract.TimerConfigDTO(TimerContract.Mode.BACKWARD, 300);

        // Act
        timer.start(config);

        // Assert: 等待計時器完成 (最多等 2 秒防死鎖)
        boolean finishedInTime = callback.completeLatch.await(2, TimeUnit.SECONDS);

        assertTrue(finishedInTime, "計時器應在指定時間內完成");
        assertTrue(callback.isCompleted, "onComplete 回調應該被觸發");
        assertEquals(300, callback.finalElapsedMs, "經過時間應該精準等於 300ms");
        
        // 驗證狀態流轉: STOPPED -> RUNNING -> STOPPED (完成時)
        assertEquals(TimerContract.State.RUNNING, callback.stateHistory.get(0));
        assertEquals(TimerContract.State.STOPPED, callback.stateHistory.get(1));
    }

    @Test
    void testPauseAndResumeStateTransitions() throws InterruptedException {
        // Arrange: 建立一個長一點的計時器，避免馬上結束
        TimerContract.TimerConfigDTO config = new TimerContract.TimerConfigDTO(TimerContract.Mode.POMODORO, 5000);

        // Act: 啟動 -> 暫停 -> 恢復 -> 停止
        timer.start(config);
        Thread.sleep(150); // 讓它跑一下
        
        timer.pause();
        Thread.sleep(100);
        
        timer.resume();
        Thread.sleep(150);
        
        timer.stop();

        // Assert: 驗證狀態流轉歷史紀錄
        assertEquals(4, callback.stateHistory.size(), "應該要有 4 次狀態變更");
        assertEquals(TimerContract.State.RUNNING, callback.stateHistory.get(0)); // 啟動
        assertEquals(TimerContract.State.PAUSED, callback.stateHistory.get(1));  // 暫停
        assertEquals(TimerContract.State.RUNNING, callback.stateHistory.get(2)); // 恢復
        assertEquals(TimerContract.State.STOPPED, callback.stateHistory.get(3)); // 停止
    }

    @Test
    void testTimerConfigValidation() {
        // Assert: 驗證 DTO 的建構子是否能成功擋住負數時間 (這就是 record 的優勢)
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> new TimerContract.TimerConfigDTO(TimerContract.Mode.BACKWARD, -100)
        );
        assertTrue(exception.getMessage().contains("時長不能為負數"));
    }
}
