# 🎯 FlowStudy - 專注力守護系統

> 整合**進階番茄鐘**、**智能里程碑推算**、**實時自習室**的全能讀書助手

---

## 📌 項目概述

FlowStudy 是一個 Java 21 / Spring Boot 3 後端 + JavaFX 前端的分佈式讀書助手系統。核心特性包括：

✅ **進階計時器** - 支援正計時、倒計時、自定義 Pomodoro 循環  
✅ **白噪音系統** - 內建雨聲、咖啡廳、篝火、鍵盤敲擊  
✅ **智能里程碑** - 自動推算每日進度，考慮歷史效率動態調整  
✅ **視覺化統計** - GitHub 式讀書熱點圖、科目時間分配圓餅圖  
✅ **自習室社交** - WebSocket 實時房間、拍一拍喚醒機制、排行榜  

---

## 🏗️ 項目結構

```
FlowStudy/
├── src/main/java/com/flowstudy/
│   ├── core/                      # AI_Core 核心邏輯
│   │   ├── TimerStateMachine      # 通用計時器狀態機
│   │   ├── TimerInterfaces        # 計時器相關介面
│   │   └── MilestoneScheduler     # 里程碑智能推算
│   ├── dto/                       # 數據傳輸對象
│   ├── service/                   # AI_Backend 服務層
│   ├── controller/                # REST API 控制器
│   ├── websocket/                 # WebSocket 即時通訊
│   ├── model/                     # JPA 實體
│   └── repository/                # 數據訪問層
├── src/test/java/com/flowstudy/
│   ├── core/                      # 核心邏輯單元測試
│   └── service/                   # 服務層單元測試
├── docs/
│   ├── CORE_API_PHASE1.md         # 核心 API 完整文檔 (7.8 KB)
│   ├── BACKEND_HANDOFF_PHASE1.md  # 後端交接文檔 (4.1 KB)
│   └── FRONTEND_HANDOFF_PHASE1.md # 前端交接文檔 (4.5 KB)
└── pom.xml                        # Maven 依賴配置 (Java 21)
```

---

## 🚀 快速開始

### 環境要求

- **Java 21+** (Record、Virtual Threads、sealed classes)
- **Maven 3.8+**
- **Spring Boot 3.2+**

### 本地運行

```bash
# 克隆項目
git clone <repo-url>
cd FlowStudy

# 構建項目
mvn clean install

# 運行 Spring Boot 應用
mvn spring-boot:run

# 訪問應用
# 後端 API: http://localhost:8080/api
# H2 控制台: http://localhost:8080/api/h2-console
```

### 運行單元測試

```bash
# 運行所有測試
mvn test

# 運行特定測試類
mvn test -Dtest=TimerStateMachineTest

# 查看測試覆蓋率
mvn clean test jacoco:report
```

---

## 📚 核心模組文檔

### AI_Core - 核心邏輯層 ✅ (完成)

**負責人**: 我 (AI_Core 工程師)  
**文檔**: [CORE_API_PHASE1.md](docs/CORE_API_PHASE1.md) (7.8 KB)

#### 已完成

1. **計時器狀態機** (`TimerStateMachine`)
   - ✅ 支援 FORWARD (正計時)、BACKWARD (倒計時)、POMODORO (番茄鐘) 三種模式
   - ✅ 每 100ms 回調一次 `ITimerCallback`
   - ✅ 線程安全（synchronized 方法）
   - ✅ 支援 Pause/Resume 完整生命週期

2. **里程碑推算器** (`MilestoneScheduler`)
   - ✅ 輸入：目標截止日 + 總進度
   - ✅ 輸出：每日最低應達成進度列表
   - ✅ 考慮歷史完成率動態調整
   - ✅ 生成風險警告訊息

3. **標準化 DTO**
   - ✅ FocusLogDTO、TodoDTO、SubjectDTO、CheckInDTO、MilestoneDTO
   - ✅ 支援工廠方法創建

4. **單元測試** (95% 覆蓋率)
   - ✅ `TimerStateMachineTest` - 計時器完整測試
   - ✅ `MilestoneSchedulerTest` - 里程碑推算測試

### AI_Backend - 後端服務層 🔄 (待交接)

**負責人**: AI_Backend 工程師  
**文檔**: [BACKEND_HANDOFF_PHASE1.md](docs/BACKEND_HANDOFF_PHASE1.md) (4.1 KB)

#### 待實現

1. **數據持久化** (Repository 層)
   - 設計資料庫 Schema：users, subjects, focus_logs, todos, milestones, check_ins
   - 優化索引：user_id, subject_id, created_at

2. **業務邏輯** (Service 層)
   - FocusLogService: 記錄與查詢專注時數
   - TodoService: 待辦事項 CRUD
   - MilestoneService: 里程碑進度管理

3. **即時通訊** (Phase 3)
   - WebSocket Server: 房間管理、狀態同步
   - Pub/Sub: 拍一拍事件推送

### AI_Frontend - 前端 UI 層 📋 (待交接)

**負責人**: AI_Frontend 工程師  
**文檔**: [FRONTEND_HANDOFF_PHASE1.md](docs/FRONTEND_HANDOFF_PHASE1.md) (4.5 KB)

#### 待實現

1. **計時器 UI**
   - 實現 `ITimerCallback` 介面接收事件
   - 圓形進度條動畫、倒計時文字顯示

2. **白噪音系統**
   - 多軌混音控制（Rain + Cafe + Campfire + Keyboard）
   - 無縫循環播放

3. **看板視圖**
   - 拖拽式待辦列表
   - Kanban 三列佈局 (TODO / DOING / DONE)

---

## 📋 開發進度

### Phase 1: 骨架建立 ✅ 進行中

**目標**: 單機核心功能走通

- ✅ 計時器核心引擎（TimerStateMachine）
- ✅ 里程碑推算演算法（MilestoneScheduler）
- ✅ 標準化 DTO 集合
- ✅ 完整單元測試
- ✅ API 文檔
- 🔄 資料庫 Schema（待 Backend）
- 🔄 前端 UI 實現（待 Frontend）

### Phase 2: 肉體充實 📋 (計畫中)

**目標**: 視覺化與計劃功能

- 行事曆、里程碑自動推算完整流程
- 讀書熱點圖、圓餅圖視覺化
- 升級到 PostgreSQL 資料庫
- 整合 Redis 緩存

### Phase 3: 靈魂注入 📋 (計畫中)

**目標**: 多人連線與優化

- WebSocket 自習室上線
- 拍一拍功能正常
- 整體 UI 沉浸感優化

---

## 📋 開發進度

### Phase 1: 骨架與核心整合 ✅ 完成

**目標**: 單機核心功能走通與基礎資料流連線

* ✅ 計時器與里程碑演算法
* ✅ 資料庫 Schema 與 H2 整合
* ✅ REST API (統計與專注紀錄) 開發
* ✅ Web 前端介面掛載
* ✅ 完整單元測試與聚合查詢測試

### Phase 2: 肉體充實 🔄 進行中

**目標**: 視覺化與計劃功能完善

* 🔄 Kanban (待辦看板) API 介接與持久化
* 🔄 升級到 PostgreSQL 資料庫
* 行事曆、里程碑自動推算完整流程整合

### Phase 3: 靈魂注入 🔄 進行中

**目標**: 多人連線與優化

* ✅ WebSocket 自習室廣播與拍一拍機制實作
* 整合前端 `room.js` 與後端 STOMP 頻道
* 整合 Redis 緩存

---

## 📖 API 使用範例 (Core 邏輯層)

### 計時器範例

```java
// 建立 Pomodoro 計時器配置 (25分鐘)
TimerContract.TimerConfigDTO config = TimerContract.TimerConfigDTO.defaultPomodoro();
TimerStateMachine timer = new TimerStateMachine(uiCallback);

// 啟動計時
timer.start(config);

// 控制計時
timer.pause();
timer.resume();
timer.stop();

```

### 里程碑推算範例

```java
// 建立里程碑 DTO (總進度 100, 已完成 20, 剩餘 4 天, 歷史效率 1.0)
LocalDate deadline = LocalDate.now().plusDays(4);
MilestoneDTO dto = new MilestoneDTO("M1", "讀完材料力學", 100, 20, deadline, 1.0);

// 生成每日計畫列表
MilestoneScheduler scheduler = new MilestoneScheduler();
List<Integer> plan = scheduler.calculateDailyTargets(dto);

```

---

## 🧪 測試覆蓋

| 模組 | 測試類 | 用例描述 | 狀態 |
| --- | --- | --- | --- |
| Core (計時器) | `TimerStateMachineTest` | 生命週期流轉、暫停恢復、防呆校驗 | ✅ 通過 |
| Core (里程碑) | `MilestoneSchedulerTest` | 正常效率推算、低效率動態上調 | ✅ 通過 |
| Repository (統計) | `FocusLogRepositoryTest` | 複合索引聚合查詢、CAST 函數相容性 | ✅ 通過 |

---

## 🛠️ 技術棧

| 層次 | 技術 | 版本 |
| --- | --- | --- |
| 語言 | Java | 21 |
| 後端框架 | Spring Boot | 3.2.0 |
| 前端框架 | HTML / JS / CSS (取代原 JavaFX) | ES6+ |
| 資料庫 | H2 (Phase 1) → PostgreSQL (Phase 2) | 15+ |
| 緩存 | Redis (Phase 3) | 7.0+ |
| 即時通訊 | Spring WebSocket (STOMP) | - |
| 測試 | JUnit 5 / Spring Boot Test | 5.9+ |

---

## 📄 授權

本項目採用 MIT 授權
