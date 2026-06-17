package com.flowstudy.core;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subjects", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subject {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id = UUID.randomUUID();

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    private String color;

    @Column(nullable = false)
    private long totalFocusMs = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Subject(UUID userId, String name, String color) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.name = name;
        this.color = color;
        this.totalFocusMs = 0;
        this.createdAt = LocalDateTime.now();
    }
}
