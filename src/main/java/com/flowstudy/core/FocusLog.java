package com.flowstudy.core;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "focus_logs", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_subject_id", columnList = "subject_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FocusLog {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id = UUID.randomUUID();

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "subject_id", nullable = false, columnDefinition = "UUID")
    private UUID subjectId;

    @Column(nullable = false)
    private long durationMs;

    @Column(nullable = false)
    private long startTimeMs;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, length = 50)
    private String tagName;

    public FocusLog(UUID userId, UUID subjectId, long durationMs, long startTimeMs, String tagName) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.subjectId = subjectId;
        this.durationMs = durationMs;
        this.startTimeMs = startTimeMs;
        this.createdAt = LocalDateTime.now();
        this.tagName = tagName;
    }
}
