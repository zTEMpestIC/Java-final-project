package com.flowstudy.core;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.flowstudy.dto.TodoDTO;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "todos", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Todo {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id = UUID.randomUUID();

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TodoDTO.TodoStatus status = TodoDTO.TodoStatus.TODO;

    @Column(nullable = false)
    private int priority = 3;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime dueDate;

    public Todo(UUID userId, String title, String description, LocalDateTime dueDate, int priority) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = TodoDTO.TodoStatus.TODO;
        this.createdAt = LocalDateTime.now();
    }

    public TodoDTO toDTO() {
        return new TodoDTO(
            this.id,
            this.userId,
            this.title,
            this.description,
            this.status,
            this.createdAt,
            this.dueDate,
            this.priority
        );
    }
}
