# 🎯 FlowStudy Phase 1 - 核心 API 文檔 (AI_Core 定義)

## 📋 目錄

1. [計時器狀態機 API](#計時器狀態機-api)
2. [里程碑推算 API](#里程碑推算-api)
3. [數據傳輸對象 (DTO)](#數據傳輸對象-dto)
4. [使用範例](#使用範例)
5. [設計決策](#設計決策)

---

## 計時器狀態機 API

### 核心介面: `ITimerCallback`

前端/UI 實現此介面以響應計時器事件：

```java
public interface ITimerCallback {
    // 每 100ms 觸發一次，用於更新 UI
    void onTick(long elapsedMs, long totalMs);
    
    // 暫停時觸發
    void onPause();
    
    // 恢復時觸發
    void onResume();
    
    // 專注或休息階段完成時觸發
    void onPhaseComplete(boolean isBreak, long nextPhaseMs);
    
    // 所有 Pomodoro 循環完成時觸發
    void onAllCyclesComplete();
}
```

### 配置: `PomodoroConfig` (Record)

```java
public record PomodoroConfig(
    long focusMinutes,      // 專注時間（分鐘）
    long breakMinutes,      // 休息時間（分鐘）
    int cycles              // 循環數
)

// 靜態工廠方法
PomodoroConfig.standard()  // 預設：25分鐘專注 + 5分鐘休息 × 4循環
```

### 計時器狀態機: `TimerStateMachine`

#### 建構式

```java
// Pomodoro 模式
new TimerStateMachine(PomodoroConfig config, ITimerCallback callback);

// 正計時或倒計時模式
new TimerStateMachine(TimerMode mode, long targetTimeMs, ITimerCallback callback);
```

#### 工作模式: `TimerMode`

```java
public enum TimerMode {
    FORWARD,    // 正計時：0 → targetTime
    BACKWARD,   // 倒計時：targetTime → 0
    POMODORO    // 番茄鐘循環
}
```

#### 計時器狀態: `TimerState`

```java
public enum TimerState {
    IDLE,       // 未啟動
    RUNNING,    // 運行中
    PAUSED,     // 暫停中
    COMPLETED,  // 已完成
    CANCELLED   // 已取消
}
```

#### 公開方法

| 方法 | 描述 | 返回值 |
|------|------|--------|
| `start()` | 啟動計時器 | void |
| `pause()` | 暫停計時器 | void |
| `resume()` | 從暫停恢復 | void |
| `stop()` | 停止並重置計時器 | void |
| `getState()` | 獲取當前狀態 | `TimerState` |
| `getElapsedMs()` | 獲取已消耗時間（毫秒） | `long` |
| `getProgressPercentage()` | 獲取當前循環進度百分比 (0-100) | `int` |
| `getCurrentCycleIndex()` | 獲取當前循環索引 (0-based) | `int` |
| `isCurrentlyBreak()` | 是否正在休息階段 | `boolean` |

---

## 里程碑推算 API

### 每日目標: `DailyMilestoneTarget` (Record)

```java
public record DailyMilestoneTarget(
    LocalDate date,              // 該日期
    double dailyTarget,          // 該日應達成進度
    double cumulativeTarget,     // 累計應達成進度
    double adjustmentFactor      // 動態調整係數（1.0 = 無調整）
)
```

### 歷史效率: `HistoricalEfficiency` (Record)

```java
public record HistoricalEfficiency(
    LocalDate date,
    double plannedProgress,       // 計畫進度
    double actualProgress,        // 實際完成進度
    double completionRate         // 完成率 (0-1)
)
```

### 推算引擎: `MilestoneScheduler`

#### 建構式

```java
// 基礎建構（無歷史數據）
new MilestoneScheduler(LocalDate deadline, double totalProgress);

// 帶歷史效率數據
new MilestoneScheduler(LocalDate deadline, double totalProgress, 
                       List<HistoricalEfficiency> history);

// 完整建構（指定開始日期）
new MilestoneScheduler(LocalDate deadline, double totalProgress,
                       List<HistoricalEfficiency> history, LocalDate startDate);
```

#### 公開方法

| 方法 | 描述 | 返回值 |
|------|------|--------|
| `generateMilestoneSchedule()` | 生成每日進度計畫 | `List<DailyMilestoneTarget>` |
| `recalculateSchedule(Map)` | 根據實際進度重新計算 | `List<DailyMilestoneTarget>` |
| `getCompletionEfficiency()` | 獲取完成效率係數 | `double` (0-1) |
| `calculateRiskLevel()` | 計算進度風險等級 | `double` (0.0=低, 1.0=高) |
| `generateWarning()` | 生成警告訊息 | `String` |

---

## 數據傳輸對象 (DTO)

### FocusLogDTO

```java
public record FocusLogDTO(
    UUID id,              // 唯一識別符
    UUID userId,          // 用戶 ID
    UUID subjectId,       // 科目 ID
    long durationMs,      // 專注時間（毫秒）
    long startTimeMs,     // 開始時間戳（毫秒）
    LocalDateTime createdAt,
    String tagName        // 如 "Pomodoro", "Self-Study"
)

// 工廠方法
FocusLogDTO.create(UUID userId, UUID subjectId, long durationMs, String tagName)
```

### TodoDTO

```java
public record TodoDTO(
    UUID id,
    UUID userId,
    String title,
    String description,
    TodoStatus status,           // TODO, DOING, DONE, CANCELLED
    LocalDateTime createdAt,
    LocalDateTime dueDate,
    int priority                 // 1-5（5 最高）
)

// 工廠方法
TodoDTO.create(UUID userId, String title, String description, 
               LocalDateTime dueDate, int priority)
```

### SubjectDTO

```java
public record SubjectDTO(
    UUID id,
    UUID userId,
    String name,
    String color,         // 科目顏色（用於視覺化）
    long totalFocusMs,    // 累計專注時間
    LocalDateTime createdAt
)

// 工廠方法
SubjectDTO.create(UUID userId, String name, String color)
```

### CheckInDTO

```java
public record CheckInDTO(
    UUID id,
    UUID userId,
    LocalDateTime checkedInAt,
    int consecutiveDays   // 連續簽到天數
)

// 工廠方法
CheckInDTO.create(UUID userId, int consecutiveDays)
```

### MilestoneDTO

```java
public record MilestoneDTO(
    UUID id,
    UUID userId,
    UUID subjectId,
    String title,
    double targetProgress,       // 目標進度（頁數、章節等）
    double currentProgress,      // 當前進度
    LocalDateTime deadline,
    int priority,
    String status                // "ON_TRACK", "AT_RISK", "COMPLETED"
)

// 工廠方法
MilestoneDTO.create(UUID userId, UUID subjectId, String title,
                    double targetProgress, LocalDateTime deadline, int priority)
```

---

## 使用範例

### 範例 1：啟動 Pomodoro 計時器

```java
// 建立回調實現
class UITimerCallback implements ITimerCallback {
    @Override
    public void onTick(long elapsedMs, long totalMs) {
        updateProgressBar(elapsedMs * 100 / totalMs);
    }
    
    @Override
    public void onPause() {
        showPauseIcon();
    }
    
    @Override
    public void onResume() {
        hidePauseIcon();
    }
    
    @Override
    public void onPhaseComplete(boolean isBreak, long nextPhaseMs) {
        playNotificationSound();
        if (isBreak) {
            showMessage("休息時間開始！");
        } else {
            showMessage("專注時間開始！");
        }
    }
    
    @Override
    public void onAllCyclesComplete() {
        showCongratulationScreen();
    }
}

// 啟動計時器
PomodoroConfig config = new PomodoroConfig(50, 10, 3); // 50分鐘專注 × 3次
ITimerCallback callback = new UITimerCallback();
TimerStateMachine timer = new TimerStateMachine(config, callback);

timer.start();
```

### 範例 2：生成每日里程碑計畫

```java
LocalDate deadline = LocalDate.of(2026, 7, 31);
MilestoneScheduler scheduler = new MilestoneScheduler(deadline, 500.0); // 500 頁目標

List<DailyMilestoneTarget> dailyPlan = scheduler.generateMilestoneSchedule();

for (DailyMilestoneTarget target : dailyPlan) {
    System.out.println(target.date() + ": " + target.dailyTarget() + " 頁");
}
```

### 範例 3：根據進度重新調整計畫

```java
Map<LocalDate, Double> actualProgress = new HashMap<>();
actualProgress.put(LocalDate.now().minusDays(2), 15.0);
actualProgress.put(LocalDate.now().minusDays(1), 10.0);

List<DailyMilestoneTarget> revisedPlan = scheduler.recalculateSchedule(actualProgress);
```

---

## 設計決策

### 1. **為什麼使用 Record？**
- Java 21 特性，簡潔不可變數據結構
- 自動生成 `equals()`, `hashCode()`, `toString()`
- 完美用於 DTO 和數據容器

### 2. **計時器線程模型**
- 每個 `TimerStateMachine` 都有獨立的守護線程（Daemon Thread）
- 100ms 精度更新一次（足夠平滑 UI，避免過度更新）
- 同步方法保護共享狀態

### 3. **里程碑推算邏輯**
- **基礎平均分配**：總進度 ÷ 剩餘天數
- **效率係數調整**：根據過去 14 天完成率動態調整
- **最小係數限制**：即使效率低，也至少提高 20%（保留彈性）
- **最後一天校正**：最後一天自動調整確保剛好達成目標

### 4. **Thread Safety**
- 所有狀態變更均使用 `synchronized`
- `volatile` 用於輕量級讀取操作
- 避免死鎖：只鎖定本物件

### 5. **異常處理策略**
- 建構式驗證參數合法性（fail-fast）
- 無狀態檢查時無異常（容錯設計）
- 使用 `IllegalArgumentException` 用於不合法輸入

---

## Phase 1 API 驗收清單

- ✅ `TimerStateMachine` 支援正計時、倒計時、Pomodoro 模式
- ✅ `ITimerCallback` 提供完整的狀態轉換事件
- ✅ `MilestoneScheduler` 支援動態進度調整
- ✅ DTO 提供標準化的數據交換格式
- ✅ 單元測試覆蓋核心邏輯
- ✅ 所有公開 API 均已文檔化

**下一步**: AI_Backend 設計資料庫 Schema；AI_Frontend 實現 UI 回調
