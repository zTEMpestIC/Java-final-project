# AI_Backend 系統設計與交接文檔 (Phase 1)

## 1. 資料庫設計 (Schema & Index)
本系統採用實體關聯模型，核心表為 `focus_logs`。
* **高頻查詢優化**：針對統計 API，我們在 `focus_logs` 表上建立了 `(user_id, end_time)` 的複合索引 (`idx_focuslog_user_time`)。這確保了在撈取特定區間（如：本週、過去一年）的數據時，資料庫能進行 Index Scan 而非 Full Table Scan。
* **相容性**：透過 Hibernate/JPA 抽象化，Phase 1 可使用 H2 進行本地快速測試，Phase 2 可無縫切換至 PostgreSQL 且無需更改業務邏輯代碼。

## 2. 統計 API 聚合策略
為了減少頻寬與伺服器記憶體消耗，所有的統計運算（如圓餅圖科目加總、熱點圖每日加總）皆交由資料庫層（DB Layer）透過 `GROUP BY` 與 `SUM` 完成。後端直接回傳計算好的 Projection 介面資料給前端。

## 3. WebSocket 即時通訊架構
採用 STOMP over WebSocket。
* **廣播模式 (Pub/Sub)**：前端訂閱 `/topic/room/{roomId}`，當有使用者狀態改變時，後端發送訊息至此頻道，房間內所有人皆可即時收到。
* **點對點模式 (P2P)**：前端訂閱 `/user/queue/nudges`。當觸發「拍一拍」時，後端會將封包精確派發給特定使用者，實現低延遲的互動提醒。
