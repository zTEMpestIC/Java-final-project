package com.flowstudy.core;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "check_ins", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_checked_in_at", columnList = "checked_in_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckIn {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id = UUID.randomUUID();

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(nullable = false)
    private LocalDateTime checkedInAt = LocalDateTime.now();

    @Column(nullable = false)
    private int consecutiveDays;

    public CheckIn(UUID userId, int consecutiveDays) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.checkedInAt = LocalDateTime.now();
        this.consecutiveDays = consecutiveDays;
    }
}
