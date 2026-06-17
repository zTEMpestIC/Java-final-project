# FlowStudy Backend 架構流程圖

## 系統架構層次

```
┌──────────────────────────────────────────────────────────────┐
│                    REST API 控制層 (Phase 1.5)                │
│  @RestController: FocusLogController, TodoController, etc.   │
└────────────────────────┬─────────────────────────────────────┘
                         │ HTTP Request/Response
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                    Service 層 (✅ 完成)                        │
│  ├─ FocusLogService    ├─ TodoService                        │
│  ├─ MilestoneService   ├─ SubjectService                     │
│  └─ CheckInService     ├─ UserService                        │
│  特色: 業務邏輯 + 事務管理 + DTO 轉換                           │
└────────────────────────┬─────────────────────────────────────┘
                         │ findByUserId, save, etc.
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                 Repository 層 (✅ 完成)                        │
│  ├─ UserRepository     ├─ TodoRepository                     │
│  ├─ SubjectRepository  ├─ CheckInRepository                  │
│  ├─ FocusLogRepository ├─ MilestoneRepository                │
│  特色: Spring Data JPA + 自定義 JPQL 查詢                     │
└────────────────────────┬─────────────────────────────────────┘
                         │ JDBC / Hibernate
                         ▼
┌──────────────────────────────────────────────────────────────┐
│              JPA 實體模型層 (✅ 完成)                          │
│  ├─ User Entity        ├─ Todo Entity                        │
│  ├─ Subject Entity     ├─ CheckIn Entity                     │
│  ├─ FocusLog Entity    ├─ Milestone Entity                   │
│  特色: @Entity, @Table, @Index, Lombok annotations           │
└────────────────────────┬─────────────────────────────────────┘
                         │ SQL
                         ▼
┌──────────────────────────────────────────────────────────────┐
│           H2 Database (Phase 1) / PostgreSQL (Phase 2)        │
│  ├─ users             ├─ focus_logs                          │
│  ├─ subjects          ├─ todos                               │
│  ├─ check_ins         ├─ milestones                          │
│  特色: 6 個表 + 6 個優化索引 + 外鍵約束                        │
└──────────────────────────────────────────────────────────────┘
```

---

## 數據流向示例

### 場景 1: 用戶完成一個 Pomodoro 並保存記錄

```
┌─────────────────────────────────────────────────────────────┐
│ UI 層 (計時器完成)                                           │
│ TimerStateMachine.onAllCyclesComplete()                     │
└─────────────────────────────────┬───────────────────────────┘
                                  │
                                  ▼ 調用
┌─────────────────────────────────────────────────────────────┐
│ Service 層                                                   │
│ FocusLogService.saveFocusLog(                                │
│   FocusLogDTO.create(userId, subjectId, 1500000, "Pomodoro")│
│ )                                                            │
└─────────────────────────────────┬───────────────────────────┘
                                  │
                                  ▼ DTO → Entity 轉換
┌─────────────────────────────────────────────────────────────┐
│ Repository 層                                                │
│ FocusLogRepository.save(focusLogEntity)                      │
└─────────────────────────────────┬───────────────────────────┘
                                  │
                                  ▼ SQL INSERT
┌─────────────────────────────────────────────────────────────┐
│ Database                                                     │
│ INSERT INTO focus_logs                                       │
│ (id, user_id, subject_id, duration_ms, start_time_ms,       │
│  created_at, tag_name)                                       │
│ VALUES (...)                                                 │
└─────────────────────────────────────────────────────────────┘

同時更新 Subject 的 totalFocusMs:
                                  │
                                  ▼ 調用
┌─────────────────────────────────────────────────────────────┐
│ Service 層                                                   │
│ SubjectService.updateTotalFocusTime(subjectId, 1500000)     │
└─────────────────────────────────┬───────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────┐
│ Database                                                     │
│ UPDATE subjects                                              │
│ SET total_focus_ms = total_focus_ms + 1500000                │
└─────────────────────────────────────────────────────────────┘
```

### 場景 2: 查詢用戶在特定日期範圍的專注記錄

```
UI 層 (用戶點擊"統計")
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│ REST API: GET /api/focus-logs/user/{userId}?start=...&end=..│
└──────────────────────────────────┬─────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────┐
│ Service 層                                                   │
│ FocusLogService.findFocusLogsByDateRange(                    │
│   userId, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 3)│
│ )                                                            │
└──────────────────────────────────┬─────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────┐
│ Repository 層                                                │
│ @Query("SELECT fl FROM FocusLog fl                           │
│        WHERE fl.userId = :userId                             │
│        AND fl.createdAt BETWEEN :startDate AND :endDate")    │
│ List<FocusLog> findByUserIdAndDateRange(...)                │
└──────────────────────────────────┬─────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────┐
│ Database (使用 idx_user_id + idx_created_at)                 │
│ SELECT * FROM focus_logs                                     │
│ WHERE user_id = ? AND created_at BETWEEN ? AND ?             │
│ [快速掃描因為有複合索引]                                      │
└──────────────────────────────────┬─────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────┐
│ Service 層 (轉換)                                             │
│ List<FocusLogDTO> - Entity → DTO 轉換                        │
└──────────────────────────────────┬─────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────┐
│ REST API (返回 JSON)                                          │
│ [                                                            │
│   {id: "uuid", userId: "uuid", durationMs: 1500000, ...},   │
│   {id: "uuid", userId: "uuid", durationMs: 2000000, ...}    │
│ ]                                                            │
└─────────────────────────────────────────────────────────────┘
```

---

## 索引優化詳解

```
Table: focus_logs

Primary Key: id (UUID)
Foreign Keys: user_id, subject_id

Indexes:
├─ idx_user_id (user_id)
│  └─ 場景: 查詢某用戶的所有記錄
│     SELECT * FROM focus_logs WHERE user_id = ?
│     效能: O(log n) 而非 O(n)
│
├─ idx_subject_id (subject_id)
│  └─ 場景: 查詢某科目的統計數據
│     SELECT SUM(duration_ms) FROM focus_logs WHERE subject_id = ?
│     效能: 快速聚合
│
└─ idx_created_at (created_at)
   └─ 場景: 查詢最近 7 天的記錄
      SELECT * FROM focus_logs WHERE created_at BETWEEN ? AND ?
      效能: 範圍查詢優化

Phase 2 優化（複合索引）:
├─ idx_user_created (user_id, created_at)
│  └─ 場景: 結合用戶 + 時間範圍查詢
│     SELECT * FROM focus_logs 
│     WHERE user_id = ? AND created_at BETWEEN ? AND ?
│     效能: 單一掃描，避免多索引組合
│
└─ idx_subject_created (subject_id, created_at)
   └─ 場景: 按科目和時間篩選
      效能: 類似
```

---

## Service 層業務邏輯

### MilestoneService 的自動狀態轉換

```java
public void updateMilestoneProgress(UUID milestoneId, double progressDelta) {
    Milestone milestone = repo.findById(milestoneId);
    double newProgress = milestone.getCurrentProgress() + progressDelta;
    
    // 業務邏輯：根據進度百分比自動更新狀態
    if (newProgress >= milestone.getTargetProgress()) {
        // ✅ 達成目標
        milestone.setStatus("COMPLETED");
    } else {
        double percentage = newProgress / milestone.getTargetProgress();
        if (percentage < 0.5) {
            // ⚠️ 進度不足 50%，風險
            milestone.setStatus("AT_RISK");
        } else {
            // ✅ 進度 50-100%，正常
            milestone.setStatus("ON_TRACK");
        }
    }
    
    repo.save(milestone);
}
```

### CheckInService 的連續簽到計算

```java
public CheckInDTO recordCheckIn(UUID userId) {
    // 1. 查詢最後一次簽到
    Optional<CheckIn> lastCheckIn = repo.findLatestCheckInByUserId(userId);
    
    // 2. 判斷是否是連續的
    int consecutiveDays = 1;  // 默認新用戶
    
    if (lastCheckIn.isPresent()) {
        LocalDate lastDate = lastCheckIn.get().getCheckedInAt().toLocalDate();
        LocalDate today = LocalDate.now();
        
        if (lastDate.equals(today)) {
            // ❌ 已簽到過
            throw new IllegalArgumentException("Already checked in today");
        } else if (lastDate.equals(today.minusDays(1))) {
            // ✅ 昨天簽到過，連續 +1
            consecutiveDays = lastCheckIn.get().getConsecutiveDays() + 1;
        }
        // 否則中斷，重新計數為 1
    }
    
    // 3. 創建新簽到記錄
    CheckIn checkIn = new CheckIn(userId, consecutiveDays);
    return repo.save(checkIn).toDTO();
}
```

---

## 事務管理 (自動)

所有 Service 方法都在 Spring 事務中運行：

```java
@Service
@RequiredArgsConstructor
public class FocusLogService {
    
    // Spring 自動添加 @Transactional
    public FocusLogDTO saveFocusLog(FocusLogDTO dto) {
        // 1. 開始事務
        // 2. 執行所有 SQL
        // 3. 自動提交或回滾（異常時）
        FocusLog entity = new FocusLog(...);
        FocusLog saved = focusLogRepository.save(entity);
        return convertToDTO(saved);
        // 事務自動提交
    }
}
```

**優點**:
- ✅ 數據一致性保證
- ✅ 異常自動回滾
- ✅ 無需手工管理連接

---

## 下一步：REST 控制器示例

```java
@RestController
@RequestMapping("/api/focus-logs")
@RequiredArgsConstructor
public class FocusLogController {
    
    private final FocusLogService focusLogService;
    
    @PostMapping
    public ResponseEntity<FocusLogDTO> createFocusLog(
            @RequestBody FocusLogDTO dto) {
        FocusLogDTO created = focusLogService.saveFocusLog(dto);
        return ResponseEntity.status(201).body(created);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FocusLogDTO>> getUserFocusLogs(
            @PathVariable UUID userId) {
        List<FocusLogDTO> logs = focusLogService.getFocusLogsByUser(userId);
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<FocusLogDTO>> getFocusLogsByDateRange(
            @PathVariable UUID userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<FocusLogDTO> logs = focusLogService.findFocusLogsByDateRange(
            userId, startDate, endDate);
        return ResponseEntity.ok(logs);
    }
}
```

---
