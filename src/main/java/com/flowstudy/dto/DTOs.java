package com.flowstudy.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 專注記錄 DTO（Data Transfer Object）
 */
public record FocusLogDTO(
    UUID id,
    UUID userId,
    UUID subjectId,
    long durationMs,
    long startTimeMs,
    LocalDateTime createdAt,
    String tagName
) {
    public static FocusLogDTO create(UUID userId, UUID subjectId, long durationMs, String tagName) {
        return new FocusLogDTO(
            UUID.randomUUID(),
            userId,
            subjectId,
            durationMs,
            System.currentTimeMillis(),
            LocalDateTime.now(),
            tagName
        );
    }
}

/**
 * 待辦事項 DTO
 */
public record TodoDTO(
    UUID id,
    UUID userId,
    String title,
    String description,
    TodoStatus status,
    LocalDateTime createdAt,
    LocalDateTime dueDate,
    int priority
) {
    public enum TodoStatus {
        TODO, DOING, DONE, CANCELLED
    }

    public static TodoDTO create(UUID userId, String title, String description,
                                LocalDateTime dueDate, int priority) {
        return new TodoDTO(
            UUID.randomUUID(),
            userId,
            title,
            description,
            TodoStatus.TODO,
            LocalDateTime.now(),
            dueDate,
            priority
        );
    }
}

/**
 * 科目 DTO
 */
public record SubjectDTO(
    UUID id,
    UUID userId,
    String name,
    String color,
    long totalFocusMs,
    LocalDateTime createdAt
) {
    public static SubjectDTO create(UUID userId, String name, String color) {
        return new SubjectDTO(
            UUID.randomUUID(),
            userId,
            name,
            color,
            0L,
            LocalDateTime.now()
        );
    }
}

/**
 * 簽到記錄 DTO
 */
public record CheckInDTO(
    UUID id,
    UUID userId,
    LocalDateTime checkedInAt,
    int consecutiveDays
) {
    public static CheckInDTO create(UUID userId, int consecutiveDays) {
        return new CheckInDTO(
            UUID.randomUUID(),
            userId,
            LocalDateTime.now(),
            consecutiveDays
        );
    }
}

/**
 * 里程碑 DTO
 */
public record MilestoneDTO(
    UUID id,
    UUID userId,
    UUID subjectId,
    String title,
    double targetProgress,
    double currentProgress,
    LocalDateTime deadline,
    int priority,
    String status
) {
    public static MilestoneDTO create(UUID userId, UUID subjectId, String title,
                                     double targetProgress, LocalDateTime deadline, int priority) {
        return new MilestoneDTO(
            UUID.randomUUID(),
            userId,
            subjectId,
            title,
            targetProgress,
            0.0,
            deadline,
            priority,
            "ON_TRACK"
        );
    }
}
