# AI_Core → AI_Backend 交接文檔

## 📦 Phase 1 資料庫 Schema 需求

根據 AI_Core 定義的 DTO，AI_Backend 需要設計以下資料表：

### 1. **focus_logs** 表
專注記錄表 - 用於記錄每次專注的詳細數據

```sql
CREATE TABLE focus_logs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    subject_id UUID NOT NULL,
    duration_ms BIGINT NOT NULL,
    start_time_ms BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    tag_name VARCHAR(50) NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    INDEX idx_user_id (user_id),
    INDEX idx_subject_id (subject_id),
    INDEX idx_created_at (created_at)
);
```

### 2. **todos** 表
待辦事項表

```sql
CREATE TABLE todos (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,  -- TODO, DOING, DONE, CANCELLED
    priority INT NOT NULL,         -- 1-5
    created_at TIMESTAMP NOT NULL,
    due_date TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);
```

### 3. **subjects** 表
科目表

```sql
CREATE TABLE subjects (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(10) NOT NULL,    -- 十六進制顏色碼
    total_focus_ms BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id)
);
```

### 4. **check_ins** 表
簽到表

```sql
CREATE TABLE check_ins (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    checked_in_at TIMESTAMP NOT NULL,
    consecutive_days INT NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_checked_in_at (checked_in_at)
);
```

### 5. **milestones** 表
里程碑表 - 用於追蹤長期目標

```sql
CREATE TABLE milestones (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    subject_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    target_progress DOUBLE NOT NULL,
    current_progress DOUBLE DEFAULT 0,
    deadline TIMESTAMP NOT NULL,
    priority INT NOT NULL,
    status VARCHAR(20) NOT NULL,   -- ON_TRACK, AT_RISK, COMPLETED
    created_at TIMESTAMP NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    INDEX idx_user_id (user_id),
    INDEX idx_deadline (deadline),
    INDEX idx_status (status)
);
```

### 6. **users** 表（必需）
用戶表 - Phase 1 基礎版本

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

## 🔑 索引優化策略

### Phase 1 優先級
- ✅ 必須：`idx_user_id` 在所有表上（快速過濾用戶數據）
- ✅ 必須：`idx_created_at` 在 `focus_logs`（時間序列查詢）
- ✅ 建議：`idx_subject_id` 在 `focus_logs`（科目統計查詢）

### Phase 2 優化
- 複合索引：`(user_id, created_at)` 在 `focus_logs`
- 複合索引：`(user_id, deadline)` 在 `milestones`

## 📝 服務層簽交

AI_Backend 需要實現以下服務方法（與 Core 協作）：

### FocusLogService
```java
public interface IFocusLogService {
    // 保存專注記錄
    FocusLogDTO saveFocusLog(FocusLogDTO dto);
    
    // 查詢特定時間範圍的記錄
    List<FocusLogDTO> findFocusLogsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate);
    
    // 計算科目總時數
    long getTotalFocusTimeBySubject(UUID subjectId);
}
```

### TodoService
```java
public interface ITodoService {
    TodoDTO createTodo(TodoDTO dto);
    TodoDTO updateTodo(TodoDTO dto);
    List<TodoDTO> getTodosByUser(UUID userId, TodoStatus status);
}
```

### MilestoneService
```java
public interface IMilestoneService {
    MilestoneDTO createMilestone(MilestoneDTO dto);
    List<MilestoneDTO> getMilestonesByUser(UUID userId);
    
    // 與 Core 協作：根據實際進度更新里程碑狀態
    void updateMilestoneProgress(UUID milestoneId, double progressDelta);
}
```

## 🚀 Phase 1 驗收標準

- ✅ 所有表已建立並通過完整性測試
- ✅ CRUD 操作全部正常
- ✅ 索引已創建並驗證性能
- ✅ 與 Core 層 DTO 的映射正確無誤
- ✅ 單元測試覆蓋 Repository 層
