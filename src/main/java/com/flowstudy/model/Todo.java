package com.flowstudy.model;

import com.flowstudy.core.contract.StudyContract.TodoDTO.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "todos", indexes = {
    @Index(name = "idx_todo_user", columnList = "user_id") // 建立 user_id 索引，加速查詢
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Todo {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 100)
    private String title;

    // 🌟 新增：對應前端傳來的 userId
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    // 🌟 修正：拿掉 nullable = false，因為前端待辦事項目前不綁定科目
    @Column(name = "subject_id", length = 36)
    private String subjectId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status; // TODO, DOING, DONE

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 🌟 新增：當儲存到資料庫前，如果沒有建立時間，自動帶入當下時間
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}