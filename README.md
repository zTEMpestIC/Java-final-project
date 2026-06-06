太棒了！我們這幾天的努力終於將核心邏輯、後端資料庫與即時通訊，還有你提供的精美前端完美結合在一起了！

觀察到一個很棒的改變：原本計畫中使用的是 **JavaFX** 作為前端，但我們實際整合了更具彈性、能直接透過瀏覽器訪問的 **Web 前端 (HTML/CSS/JS)**，這讓專案的完整度大幅提升！

我已經根據我們目前**真實的開發進度與程式碼結構**，為你全面更新了這份 `README.md`。你可以直接將以下內容複製並覆蓋專案根目錄的 README 檔案：

---

# 🎯 FlowStudy - 專注力守護系統

> 整合**進階番茄鐘**、**智能里程碑推算**、**實時自習室**的全能讀書助手

---

## 📌 項目概述

FlowStudy 是一個 **Java 21 / Spring Boot 3 後端** 搭配 **Web 前端 (HTML/JS/CSS)** 的分佈式讀書助手系統。核心特性包括：

✅ **進階計時器** - 支援正計時、倒計時、自定義 Pomodoro 循環

✅ **白噪音系統** - 內建雨聲、咖啡廳、篝火、鍵盤敲擊

✅ **智能里程碑** - 自動推算每日進度，考慮歷史效率動態調整

✅ **視覺化統計** - GitHub 式讀書熱點圖、科目時間分配圓餅圖

✅ **自習室社交** - WebSocket 實時房間、拍一拍喚醒機制、排行榜

---

## 🏗️ 項目結構

```text
FlowStudy 
│  pom.xml
│  README.md
│  
├─docs
│      BACKEND_HANDOFF_PHASE1.md
│      CORE_API_PHASE1.md
│      
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─flowstudy
│  │  │          │  FlowStudyApplication.java
│  │  │          │  
│  │  │          ├─controller
│  │  │          │      FocusApiController.java
│  │  │          │   
│  │  │          ├─core
│  │  │          │  │  MilestoneScheduler.java
│  │  │          │  │  TimerStateMachine.java
│  │  │          │  │  
│  │  │          │  └─contract
│  │  │          │          MilestoneAndSocialContract.java
│  │  │          │          StudyContract.java
│  │  │          │          TimerContract.java
│  │  │          │          
│  │  │          ├─model
│  │  │          │      FocusLog.java
│  │  │          │      Subject.java
│  │  │          │      Todo.java
│  │  │          │      
│  │  │          ├─repository
│  │  │          │      FocusLogRepository.java
│  │  │          │      
│  │  │          └─websocket
│  │  │                 StudyRoomController.java
│  │  │                 WebSocketConfig.java
│  │  │                 
│  │  └─resources
│  │      │   application.yml
│  │      │
│  │      └──static
│  │          │   index.html
│  │          │   ws-test.html
│  │          ├──css
│  │          │       style.css
│  │          ├──js
│  │          │       api.js
│  │          │       timer.js
│  │          │       audio.js
│  │          │       kanban.js
│  │          │       room.js
│  │          │       stats.js
│  │          │       milestone.js
│  │          └──assets
│  │              └──sounds  
│  │                      rain.mp3
│  │                      cafe.mp3
│  │                      campfire.mp3
│  │                      keyboard.mp3
│  │                 
│  └─test
│      └─java
│          └─com
│              └─flowstudy
│                  ├─core
│                  │      MilestoneSchedulerTest.java
│                  │      TimerStateMachineTest.java
│                  │      
│                  └─repository
│                          FocusLogRepositoryTest.java     

```

---

## 🚀 快速開始

### 環境要求

* **Java 21+** (Record、Virtual Threads、sealed classes)
* **Maven 3.8+**
* **Spring Boot 3.2+**

### 本地運行

```bash
# 克隆項目
git clone <repo-url>

# 構建項目
mvn clean install

# 運行 Spring Boot 應用
mvn spring-boot:run

```

### 訪問應用

* 🌟 **前端主程式 (整合版)**: [http://localhost:8080/](https://www.google.com/search?q=http://localhost:8080/)
* 🧪 **WebSocket 測試艙**: [http://localhost:8080/ws-test.html](https://www.google.com/search?q=http://localhost:8080/ws-test.html)
* 🗄️ **H2 資料庫控制台**: [http://localhost:8080/h2-console](https://www.google.com/search?q=http://localhost:8080/h2-console)

### 運行單元測試

```bash
# 運行所有測試
mvn clean test

# 查看測試覆蓋率
mvn clean test jacoco:report

```

---

## 📚 核心模組文檔

### AI_Core - 核心邏輯層 ✅ (已完成)

**負責人**: AI_Core 工程師

**狀態**: 已整合並提供 `contract` 介面約定。

1. **計時器狀態機** (`TimerStateMachine`)
* ✅ 支援 FORWARD、BACKWARD、POMODORO 模式
* ✅ 每 100ms 觸發回調，線程安全


2. **里程碑推算器** (`MilestoneScheduler`)
* ✅ 根據剩餘天數與歷史效率推算每日進度


3. **標準化契約 (Contracts)**
* ✅ 運用 Java 21 `record` 建立不可變 DTO (TimerContract, StudyContract, MilestoneAndSocialContract)



### AI_Backend - 後端服務層 ✅ (Phase 1 已完成)

**負責人**: AI_Backend 架構師

**狀態**: 已打通資料庫與 REST/WebSocket API。

1. **數據持久化** (JPA / H2)
* ✅ `FocusLog` 建立複合索引 `(user_id, end_time)` 優化統計查詢


2. **統計 API 聚合** (`FocusApiController`)
* ✅ 使用 `GROUP BY` 與 `CAST` 直接在資料庫層運算圓餅圖與熱點圖數據


3. **即時通訊** (`WebSocketConfig` & `StudyRoomController`)
* ✅ STOMP 協定，支援房間狀態廣播 (`/topic/room`) 與拍一拍精準推送 (`/topic/nudge`)



### AI_Frontend - 前端 UI 層 ✅ (已整合)

**負責人**: AI_Frontend 工程師

**狀態**: 已作為靜態資源部署於 Spring Boot 內部。

1. **沉浸式計時器 UI**
* ✅ 圓形進度條動畫、白噪音多軌混音控制


2. **視覺化與自習室**
* ✅ Chart.js 繪製統計圖表、WebSocket 狀態同步


3. **API 橋接**
* ✅ `api.js` 負責調用 REST API，具備 LocalStorage 防呆降級機制



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
