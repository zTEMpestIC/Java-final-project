# Java-final-project

### 參考網站/app
app:YPT、專注清單、專注森林、人升
網站：lofi town

## 功能
- 簽到系統
- 計時器 / 進階番茄鐘 / 正計時與倒數：支援傳統的 25/5 分鐘循環，或讓使用者自訂結構（例如：專注 50 分鐘、休息 10 分鐘）。
- 科目管理：新增目前在讀的科目
- 撥放音樂or環境音伴讀（白噪音）：內建下雨聲、咖啡廳、篝火、甚至程式碼敲擊聲，幫助使用者快速進入「心流（Flow）」狀態。
- 待辦清單（To-Do List）與看板
- 階段性里程碑：設定長遠目標（例如：7 月底前讀完某本厚重教材），系統會自動倒數並推算每日應達成的最低進度。
- 行事曆功能
- 讀書計畫：制定每日計畫，完成後可標記完成
- 熱點圖與時數統計：仿照 GitHub Contribution Graph 的「讀書格子」，書讀得越久，當天的格子顏色就越深；並提供日、週、月的圓餅圖，分析各科目的時間分配。
- 自習室：建立房間、加入房間、顯示在線成員、顯示讀書狀態、提醒讀書、排行榜(未來加入DC畫面共享?)
- 喚醒隊友：若看到夥伴偷懶沒上線，可以點擊「拍一拍」或發送提醒，互相督促。

以下是我們的團隊配置與任務分工。我將把工作分發給 3 位 AI 工程師：**前端與介面專家 (AI_Frontend)**、**後端與資料庫架構師 (AI_Backend)**，以及**核心邏輯與演算法工程師 (AI_Core)**。

---

## 📋 系統架構拆解與 AI 團隊分工

### 1. 核心專注與多媒體系統 (Focus & Multimedia System)

* **功能範疇：** 進階番茄鐘/正倒計時、科目管理、白噪音播放。
* **負責人：** `AI_Core` (邏輯)、`AI_Frontend` (UI/UX)

### 2. 任務與時程規劃系統 (Task & Schedule System)

* **功能範疇：** 待辦清單 (To-Do)、看板、行事曆讀書計畫、階段性里程碑自動推算。
* **負責人：** `AI_Core` (推算演算法)、`AI_Backend` (資料持久化)

### 3. 數據統計與視覺化系統 (Data Analytics & Visualization System)

* **功能範疇：** 簽到系統、GitHub 風格讀書熱點圖 (Contribution Graph)、日/週/月科目時間分配圓餅圖。
* **負責人：** `AI_Frontend` (圖表渲染)、`AI_Backend` (大數據聚合查詢)

### 4. 多人線上自習室與社交系統 (Social Study Room System)

* **功能範疇：** 自習室房間管理、在線狀態同步、喚醒隊友 (拍一拍)、排行榜。
* **負責人：** `AI_Backend` (WebSocket/網路通訊)、`AI_Frontend` (動態介面)

---

## 🛠️ AI 員工任務派發書

### 🧑‍💻 AI_Core (核心邏輯與演算法工程師)

> **PM 叮嚀：** 你負責處理時間與目標的數學模型。請確保時間計算的精準度，以及里程碑動態推算的邏輯彈性。

* **任務 A：通用計時器核心引擎**
* 開發一個支援正計時、倒計時、以及自定義番茄鐘循環（例如 $50 \text{ mins} / 10 \text{ mins}$）的 State Machine。
* 提供計時狀態的 Callback 介面（Start, Pause, Tick, Finished），以便前端綁定 UI。


* **任務 B：里程碑智能推算演算法**
* 設計一個排程演算法：輸入「目標截止日」與「剩餘總進度（如頁數/章節）」，動態計算出「每日最低應達成進度」。
* 需考慮歷史完成效率，若使用者前幾天落後，演算法需動態微調後續日期的權重。



---

### 🗄️ AI_Backend (後端與資料庫架構師)

> **PM 叮嚀：** 這個專案有高頻率的時間封包更新（自習室動態）與豐富的統計需求。請務必設計好資料庫 Schema，並確保社交即時通訊的低延遲。

* **任務 A：資料庫資料庫 Schema 設計 (SQL/NoSQL)**
* 設計用戶、科目、專注歷史紀錄（Focus Logs，精確到秒）、待辦事項、簽到表等資料表。
* 特別優化 Focus Logs 的索引（Index），以便動態撈取特定區間的數據給統計系統。


* **任務 B：WebSocket 即時自習室後端**
* 建立 WebSocket Server，處理使用者登入房間、切換讀書狀態（如：讀書中、休息中、斷線）。
* 實作「喚醒隊友（拍一拍）」的即時事件推送機制（Pub/Sub 模式）。


* **任務 C：統計 API 開發**
* 撰寫高效能的聚合查詢（Aggregation Query），提供特定時間內各科目的總時數比例，以及過去一年每日專注時數（用於熱點圖）。



---

### 🎨 AI_Frontend (前端與介面專家)

> **PM 叮嚀：** 使用者能不能進入「心流」，你的介面設計是關鍵。請參考 Lofi Town 與 YPT，做出沉浸感強且流暢的 UI。

* **任務 A：沉浸式計時器與白噪音面板**
* 實作計時器主視覺（支援圓形進度條動態倒數）。
* 整合 Java 音訊 API (如 `javax.sound.sampled` 或第三方庫)，實作多軌道白噪音（Rain, Cafe, Campfire, Keyboard）的獨立音量控制與混音播放。


* **任務 B：數據視覺化組件**
* 手動繪製或整合圖表庫（如 JFreeChart，若是 JavaFX 則使用內建 Chart API），實作 **GitHub 讀書熱點圖** 與 **時數圓餅圖**。


* **任務 C：自習室與看板 (Kanban) 介面**
* 設計自習室的多格畫面，動態顯示房間內所有成員的頭像與當前狀態（用顏色或動態圖示區分）。
* 設計拖拽式（或點擊切換式）的待辦看板（To-Do / Doing / Done）。



---

## 📈 PM 的專案階段里程碑 (Milestones)

為了確保專案不爛尾，我們採取**敏捷開發 (Agile)** 模式，分三個階段推進：

| 階段 | 目標 | 產出檢查點 |
| --- | --- | --- |
| **Phase 1: 骨架建立** | 單機核心功能走通 | 計時器能動、白噪音能播、待辦清單能新增、基本資料庫能儲存。 |
| **Phase 2: 肉體充實** | 視覺化與計劃功能 | 行事曆、里程碑自動推算、讀書熱點圖與圓餅圖渲染完成。 |
| **Phase 3: 靈魂注入** | 多人連線與優化 | WebSocket 自習室上線、拍一拍功能正常、整體 UI 沉浸感優化。 |

---

## 📢 PM 給團隊的開發備忘錄 (Tech Stack Notes)

1. **介面技術選擇：** 建議使用 **JavaFX**（比起 Swing，JavaFX 的 CSS 樣式支援更能完美還原 Lofi town 的精緻感與 Forest 的動畫表現）。
2. **即時通訊：** 自習室與拍一拍功能，後端推薦使用 **Spring Boot WebSocket** 或 **Java-WebSocket** 庫來實作。
3. **音訊處理：** 白噪音需支援無縫循環播放（Seamless Looping），避免音訊結束重播時出現卡頓感，影響使用者心流。

各位 AI 員工，請根據你們分配到的職責，**推選出你們認為在 Phase 1 需要優先定義的 API 介面或資料結構（DTO）**，我們五分鐘後進行第一次 Sprint Planning。

你有任何想要調整的功能優先順序，或是技術選型的想法嗎？

##骨架  
FlowStudy/  
│  
├── pom.xml  
├── README.md  
│  
├── docs/  
│　　├── use-case-diagram.md  
│　　├── class-diagram.md  
│　　└── ai-development-log.md  
│  
├── src/  
│　　├── main/  
│　　│　　├── java/  
│　　│　　│　　└── com/  
│　　│　　│　　　　└── flowstudy/  
│　　│　　│　　　　　　├── FlowStudyApplication.java  
│　　│　　│　　　　　　│  
│　　│　　│　　　　　　├── controller/  
│　　│　　│　　　　　　│　　├── FocusLogController.java  
│　　│　　│　　　　　　│　　├── TodoController.java  
│　　│　　│　　　　　　│　　└── StudyRoomController.java  
│　　│　　│　　　　　　│  
│　　│　　│　　　　　　├── service/  
│　　│　　│　　　　　　│　　├── FocusLogService.java  
│　　│　　│　　　　　　│　　├── TodoService.java  
│　　│　　│　　　　　　│　　└── StudyRoomService.java  
│　　│　　│　　　　　　│  
│　　│　　│　　　　　　├── model/  
│　　│　　│　　　　　　│　　├── FocusLog.java  
│　　│　　│　　　　　　│　　├── Todo.java  
│　　│　　│　　　　　　│　　└── StudyRoom.java  
│　　│　　│　　　　　　│  
│　　│　　│　　　　　　└── core/  
│　　│　　│　　　　　　　　├── TimerStateMachine.java  
│　　│　　│　　　　　　　　└── MilestoneScheduler.java  
│　　│　　│  
│　　│   └── resources/   
│　　│　　　　　　├── application.properties   
│　　│　　　　　　└── static/   
│　　│　　　　　　├── index.html  
│　　│　　　　　　├── css/  
│　　│　　　　　　│　　└── style.css  
│　　│　　　　　　├── js/  
│　　│　　　　　　│　　├── timer.js  
│　　│　　　　　　│　　├── audio.js  
│　　│　　　　　　│　　├── kanban.js  
│　　│　　　　　　│　　└── room.js  
│　　│　　　　　　└── assets/  
│　　│　　　　　　　　└── sounds/  
│　　│　　　　　　　　　　　├── rain.mp3  
│　　│　　　　　　　　　　　├── cafe.mp3  
│　　│　　　　　　　　　　　├── campfire.mp3  
│　　│　　　　　　　　　　　└── keyboard.mp3  

## 前端
static/  
├── index.html  
├── css/  
│　　└── style.css  
├── js/  
│　　├── api.js  
│　　├── timer.js  
│　　├── audio.js  
│　　├── kanban.js  
│　　└── room.js  
└── assets/  
　　└── sounds/   
　　　　├── rain.mp3  
　　　　├── cafe.mp3  
　　　　├── campfire.mp3  
　　　　└── keyboard.mp3  
