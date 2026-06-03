# AI_Core → AI_Frontend 交接文檔

## 🎨 Phase 1 前端介面需求

根據 AI_Core 定義的計時器與里程碑 API，AI_Frontend 需要實現以下 UI 組件：

---

## 計時器 UI 組件

### 1. **計時器顯示面板**

實現 `ITimerCallback` 介面以接收來自 `TimerStateMachine` 的事件：

```java
public class TimerUIComponent implements ITimerCallback {
    private ProgressBar progressBar;
    private Label timeDisplay;
    private Label phaseLabel;
    
    @Override
    public void onTick(long elapsedMs, long totalMs) {
        // 更新進度條和時間顯示
        int progress = (int)(elapsedMs * 100 / totalMs);
        progressBar.setProgress(progress);
        
        long remainingS = (totalMs - elapsedMs) / 1000;
        timeDisplay.setText(formatTime(remainingS));
    }
    
    @Override
    public void onPause() {
        // 顯示暫停圖示
    }
    
    @Override
    public void onResume() {
        // 隱藏暫停圖示，繼續動畫
    }
    
    @Override
    public void onPhaseComplete(boolean isBreak, long nextPhaseMs) {
        // 播放過渡動畫
        // 更新標籤：「專注中」→「休息中」或反之
        // 播放通知音
    }
    
    @Override
    public void onAllCyclesComplete() {
        // 顯示完成畫面
        // 播放慶祝動畫
    }
}
```

### 2. **進度條設計**

支援以下視覺化方式：
- ✅ 圓形進度條（推薦 - 沉浸感最強）
- ✅ 線性進度條（備選）
- ✅ 文字倒數 (MM:SS 格式)

### 3. **Pomodoro 循環指示器**

顯示當前循環進度：
```
[●] [◯] [◯] [◯]  ← 第 1 個已完成
     [●] - 正在進行
     [◯] - 未開始
```

---

## 白噪音播放系統

### 音訊來源配置

在 `src/main/resources/sounds/` 建立以下音訊文件：

```
sounds/
├── rain.mp3              // 下雨聲
├── cafe.mp3              // 咖啡廳
├── campfire.mp3          // 篝火
└── keyboard.mp3          // 鍵盤敲擊
```

### Audio Manager Interface

```java
public interface IAudioManager {
    // 開始播放背景音
    void playAmbientSound(String soundName);
    
    // 停止播放
    void stopAmbientSound();
    
    // 設置音量 (0.0 - 1.0)
    void setVolume(String soundName, double volume);
    
    // 是否正在播放
    boolean isPlaying(String soundName);
    
    // 列出所有可用的音訊
    List<String> getAvailableSounds();
}
```

### 實現要點

- ⚠️ **無縫循環播放**：使用 OGG Vorbis 格式避免 MP3 重播卡頓
- ⚠️ **多軌混音**：支援同時播放多個音訊（如 Rain + Cafe）
- ⚠️ **漸進式淡出**：關閉時不要突然停止，應該 2 秒內逐漸降低音量

---

## 待辦清單 UI 組件

### 看板 (Kanban) 視圖

實現拖拽式待辦列表：

```
┌─────────────────────────────────────────┐
│  TODO (3)        │  DOING (1)   │  DONE (5) │
├─────────────────────────────────────────┤
│                  │              │           │
│ ┌───────────┐  │ ┌─────────┐ │ ┌─────┐  │
│ │ 完成第3章 │  │ │ 練習題1 │ │ │ 作業1 │ │
│ └───────────┘  │ └─────────┘ │ └─────┘  │
│                  │              │           │
│ ┌───────────┐  │              │ ┌─────┐  │
│ │ 完成第4章 │  │              │ │ 作業2 │ │
│ └───────────┘  │              │ └─────┘  │
│                  │              │           │
└─────────────────────────────────────────┘
```

### 待辦項卡片

每個卡片需要顯示：
- 標題
- 優先級（1-5 星）
- 截止日期倒數
- 拖拽握把

---

## 里程碑進度顯示

### 進度條 + 風險指示器

```
📚 計算機概論 - 第 7 章
████████░░ 80%  ✅ ON_TRACK
預計 7/31 完成 | 每日應進度：5 頁

📖 高等數學 - 第 3 章
█████░░░░░ 50%  ⚠️ AT_RISK
預計 7/25 完成 | ⏰ 每日應進度：8 頁
```

### 風險等級視覺化

| 風險等級 | 顏色 | 圖示 | 說明 |
|---------|------|------|------|
| 低 (0.0-0.3) | 🟢 綠 | ✅ | 進度超前或按計畫 |
| 中 (0.3-0.7) | 🟡 黃 | ⏰ | 進度略有落後 |
| 高 (0.7-1.0) | 🔴 紅 | ⚠️ | 進度嚴重落後 |

### 自動警告系統

根據 `MilestoneScheduler.generateWarning()` 的返回值，在畫面頂部顯示：

```
⚠️ 進度嚴重落後！建議加快讀書速度或延長每日時間。
```

---

## 簽到系統 UI

### 簽到日曆

顯示過去 30 天的簽到情況：

```
6月     1□ 2□ 3☑ 4☑ 5☑ 6□ 
        7☑ 8☑ 9□ 10☑11☑12☑
        ...
        
連續簽到：12 天 🔥
```

---

## 數據版本交接

### Phase 1 → Phase 2 UI 升級清單

| 功能 | Phase 1 | Phase 2 | 備註 |
|------|---------|---------|------|
| 計時器 | ✅ 基礎 | ✅ 皮膚主題 | 支援明/暗模式 |
| 白噪音 | ✅ 4 種 | ✅ + 10 種新音訊 | 社群上傳音訊 |
| 看板 | ✅ 靜態 | ✅ 拖拽完整 | 支援鍵盤操作 |
| 里程碑 | ✅ 文字 | ✅ 圖表 | 加入熱點圖 |
| 簽到 | ✅ 計數器 | ✅ 成就系統 | 達成里程碑獲得徽章 |

---

## 實現建議

### JavaFX 選型理由

1. **CSS 支援** - 能直接套用 Lofi Town 的視覺風格
2. **內建圖表 API** - 無需外部依賴
3. **動畫框架** - Timeline/Transition 實現平滑過渡
4. **跨平台** - Windows/macOS/Linux

### 關鍵第三方庫（建議）

```xml
<!-- 圖表渲染 -->
<dependency>
    <groupId>de.gsi.chart</groupId>
    <artifactId>chartfx-chart</artifactId>
    <version>11.2.1</version>
</dependency>

<!-- 音訊播放 -->
<dependency>
    <groupId>org.tritonus</groupId>
    <artifactId>tritonus-share</artifactId>
    <version>0.3.6</version>
</dependency>
```

---

## Phase 1 驗收清單

- ✅ 計時器 UI 能正確響應 `ITimerCallback` 事件
- ✅ 圓形進度條動畫流暢（60 FPS）
- ✅ 白噪音無縫循環播放（測試 5 分鐘無中斷）
- ✅ 待辦清單能新增/刪除/標記完成
- ✅ 里程碑進度條正確顯示
- ✅ UI 響應時間 < 100ms
- ✅ 整體風格一致（黑暗 Lofi 主題）
