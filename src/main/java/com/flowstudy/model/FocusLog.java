package com.flowstudy.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "focus_logs", indexes = {
    // 🌟 核心優化：針對「某用戶在某段時間內的專注紀錄」進行複合索引
    @Index(name = "idx_focuslog_user_time", columnList = "user_id, end_time")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FocusLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "duration_ms", nullable = false)
    private long durationMs;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
}