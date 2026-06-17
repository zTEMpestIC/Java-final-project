# FlowStudy UML 與 Workflow

本文件說明 FlowStudy 專案的系統元件、資料流與核心序列流程。

> Mermaid 圖可在 VS Code Mermaid preview、GitHub README 或其他支援 Mermaid 的編輯器中直接渲染。

---

## 1. Component Workflow Diagram

```mermaid
flowchart TD
    Browser[Browser UI<br/>(index.html + JS)]
    Static[Static Resources<br/>(HTML/CSS/JS)]
    API[REST API Layer<br/>(/api/*)]
    WS[WebSocket Layer<br/>(/ws-studyroom)]
    Controller[Spring Controllers]
    Core[Core Logic]
    Repo[Repository Layer]
    DB[H2 Database]

    subgraph Frontend
        Browser --> Static
        Browser --> API
        Browser --> WS
    end

    subgraph Backend
        API --> Controller
        WS --> Controller
        Controller --> Repo
        Controller --> Core
        Repo --> DB
    end

    subgraph "Core Services"
        Core --> TimerStateMachine[TimerStateMachine]
        Core --> MilestoneScheduler[MilestoneScheduler]
    end

    subgraph "WebSocket Infrastructure"
        Controller --> Broker[STOMP Broker / SimpMessagingTemplate]
        Broker --> Browser
    end

    Static -->|served by| FlowStudyApplication[Spring Boot Application]
    Controller -->|persist/retrieve| DB
    Browser -->|subscribe/publish| WS
    Browser -->|invoke| TimerStateMachine
    Browser -->|request| MilestoneScheduler
```

### 說明

- `Browser UI` 包含前端靜態頁面與 JavaScript 邏輯。
- `REST API Layer` 處理 `FocusLog` 的儲存與統計查詢。
- `WebSocket Layer` 處理自習室狀態更新與拍一拍通知。
- `Core Logic` 包含計時器狀態機與里程碑目標推算。
- `Repository Layer` 存取 `FocusLog` / `Subject` 等資料到 H2 資料庫。

---

## 2. 完整 Mermaid Sequence Diagram

```mermaid
sequenceDiagram
    participant Browser as Browser UI
    participant Frontend as Frontend JS
    participant API as FocusApiController
    participant Repo as FocusLogRepository
    participant DB as H2 Database
    participant WS as StudyRoomController
    participant Broker as STOMP Broker
    participant Timer as TimerStateMachine
    participant Milestone as MilestoneScheduler

    note over Browser,API: 1. 儲存專注紀錄流程
    Browser->>Frontend: 使用者觸發儲存紀錄
    Frontend->>API: POST /api/focus-logs
    API->>API: 查詢 Subject
    alt Subject 不存在
        API->>DB: INSERT Subject
        DB-->>API: Subject persisted
    end
    API->>Repo: save(FocusLog)
    Repo->>DB: INSERT FocusLog
    DB-->>Repo: saved
    Repo-->>API: FocusLog persisted
    API-->>Frontend: {status: success, id}
    Frontend-->>Browser: 顯示儲存成功

    alt 2. 取得統計資料流程
        Browser->>Frontend: 查詢統計資料
        Frontend->>API: GET /api/statistics/overview?userId=...
        API->>Repo: sumDurationBySubject(...)
        Repo->>DB: SELECT GROUP BY subject
        DB-->>Repo: pie chart data
        API->>Repo: getDailyFocusHeatmap(...)
        Repo->>DB: SELECT daily heatmap
        DB-->>Repo: heatmap data
        Repo-->>API: aggregated results
        API-->>Frontend: {subjectRatio, heatmap}
        Frontend-->>Browser: 顯示圖表
    end

    alt 3. WebSocket 房間狀態更新流程
        Browser->>Frontend: 自習室狀態改變
        Frontend->>WS: SEND /app/room.status
        WS->>Broker: convertAndSend(/topic/room/{roomId})
        Broker-->>Browser: room update
        Browser-->>Frontend: 更新房間成員狀態
    end

    alt 4. WebSocket 拍一拍流程
        Browser->>Frontend: 按下拍一拍
        Frontend->>WS: SEND /app/room.nudge
        WS->>Broker: convertAndSend(/topic/nudge/{targetUserId})
        Broker-->>Browser: nudge message
        Browser-->>Frontend: 顯示拍一拍通知
    end

    alt 5. 計時器啟動與回調流程
        Browser->>Frontend: 啟動計時器
        Frontend->>Timer: new TimerStateMachine(callback)
        Frontend->>Timer: start(TimerConfigDTO)
        Timer->>Timer: schedule tick every 100ms
        loop 每 100ms
            Timer->>Timer: tick()
            Timer-->>Frontend: onTick(remainingMs, elapsedMs)
        end
        alt 計時完成
            Timer-->>Frontend: onComplete(mode)
        end
    end

    alt 6. 里程碑目標計算流程
        Browser->>Frontend: 讀取或調整里程碑
        Frontend->>Milestone: calculateDailyTargets(MilestoneDTO)
        Milestone-->>Frontend: daily target list
        Frontend-->>Browser: 顯示每日目標
    end
```

## 3. Class Diagram

```mermaid
classDiagram
    class FlowStudyApplication {
      +main(args:String[])
    }

    class FocusApiController {
      +saveFocusLog(payload: FocusLogPayload)
      +getTodayFocus(userId:String)
      +getStatistics(userId:String)
    }

    class StudyRoomController {
      +updateStatus(message: RoomStatusMessage)
      +nudgeTeammate(message: NudgeMessage)
    }

    class WebSocketConfig {
      +configureMessageBroker(config: MessageBrokerRegistry)
      +registerStompEndpoints(registry: StompEndpointRegistry)
    }

    class TimerStateMachine {
      -state: TimerContract.State
      -mode: TimerContract.Mode
      -timeRemainingMs: long
      -timeElapsedMs: long
      +start(config: TimerContract.TimerConfigDTO)
      +pause()
      +resume()
      +stop()
    }

    class MilestoneScheduler {
      +calculateDailyTargets(dto: MilestoneAndSocialContract.MilestoneDTO)
    }

    class FocusLogRepository {
      +sumDurationBySubject(userId:String,startDate:LocalDateTime,endDate:LocalDateTime)
      +getDailyFocusHeatmap(userId:String,oneYearAgo:LocalDateTime)
    }

    class FocusLog {
      -id:String
      -userId:String
      -subject:Subject
      -durationMs:long
      -endTime:LocalDateTime
    }

    class Subject {
      -id:String
      -name:String
      -colorCode:String
    }

    class Todo {
      -id:String
      -title:String
      -subjectId:String
      -status:TaskStatus
      -createdAt:LocalDateTime
    }

    class TimerContract {
      <<interface>>
    }
    class StudyContract {
      <<interface>>
    }
    class MilestoneAndSocialContract {
      <<interface>>
    }

    FocusApiController --> FocusLogRepository
    FocusApiController --> FocusLog
    FocusLog --> Subject
    StudyRoomController --> SimpMessagingTemplate
    TimerStateMachine --> TimerContract
    MilestoneScheduler --> MilestoneAndSocialContract
    WebSocketConfig --> StudyRoomController

    StudyContract <|-- SubjectDTO
    StudyContract <|-- TodoDTO
    StudyContract <|-- FocusLogDTO
    TimerContract <|-- TimerConfigDTO
    TimerContract <|-- ITimerCallback
    MilestoneAndSocialContract <|-- MilestoneDTO
    MilestoneAndSocialContract <|-- CheckInDTO
```

### 說明

- `Browser UI` 表示使用者在瀏覽器端的動作與資料顯示。
- `Frontend JS` 表示靜態資源中的前端邏輯。
- `FocusApiController` 與 `FocusLogRepository` 代表 REST API 路徑與資料存取。
- `StudyRoomController` 代表 WebSocket 訊息接收與推播。
- `TimerStateMachine` 與 `MilestoneScheduler` 為專案核心運算元件。

---

## 3. 建議使用方式

- 如果你要將這份文件放到 GitHub，可直接使用 Mermaid 支援的 Markdown。
- 若要在 VS Code 中預覽，安裝 `Markdown Preview Mermaid Support` 或使用內建 Mermaid preview。
