package com.flowstudy.core;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "milestones", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_deadline", columnList = "deadline"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Milestone {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id = UUID.randomUUID();

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "subject_id", nullable = false, columnDefinition = "UUID")
    private UUID subjectId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private double targetProgress;

    @Column(nullable = false)
    private double currentProgress = 0;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private int priority;

    @Column(nullable = false, length = 20)
    private String status = "ON_TRACK";

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Milestone(UUID userId, UUID subjectId, String title, double targetProgress, 
                    LocalDateTime deadline, int priority) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.subjectId = subjectId;
        this.title = title;
        this.targetProgress = targetProgress;
        this.currentProgress = 0;
        this.deadline = deadline;
        this.priority = priority;
        this.status = "ON_TRACK";
        this.createdAt = LocalDateTime.now();
    }
}
