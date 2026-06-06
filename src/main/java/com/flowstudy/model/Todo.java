package com.flowstudy.model;

import com.flowstudy.core.contract.StudyContract.TodoDTO.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "todos", indexes = {
    @Index(name = "idx_todo_subject", columnList = "subject_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Todo {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "subject_id", nullable = false, length = 36)
    private String subjectId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status; // TODO, DOING, DONE

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}