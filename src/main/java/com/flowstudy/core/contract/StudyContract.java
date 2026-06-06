package com.flowstudy.core.contract;

import java.time.LocalDateTime;
import java.util.UUID;

public interface StudyContract {

    // 科目 DTO
    record SubjectDTO(String id, String name, String colorCode) {
        public static SubjectDTO create(String name, String colorCode) {
            return new SubjectDTO(UUID.randomUUID().toString(), name, colorCode);
        }
    }

    // 待辦事項 DTO (配合看板視圖的 TODO / DOING / DONE)
    record TodoDTO(String id, String title, String subjectId, TaskStatus status, LocalDateTime createdAt) {
        public enum TaskStatus { TODO, DOING, DONE }

        public static TodoDTO createNew(String title, String subjectId) {
            return new TodoDTO(UUID.randomUUID().toString(), title, subjectId, TaskStatus.TODO, LocalDateTime.now());
        }
        
        // 提供狀態變更的便捷方法 (因為 record 是不可變的，所以回傳新的實例)
        public TodoDTO withStatus(TaskStatus newStatus) {
            return new TodoDTO(this.id, this.title, this.subjectId, newStatus, this.createdAt);
        }
    }

    // 專注紀錄 DTO
    record FocusLogDTO(String id, String subjectId, long durationMs, LocalDateTime endTime) {
        public static FocusLogDTO createLog(String subjectId, long durationMs) {
            return new FocusLogDTO(UUID.randomUUID().toString(), subjectId, durationMs, LocalDateTime.now());
        }
    }
}