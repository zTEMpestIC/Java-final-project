package com.flowstudy.dto;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.flowstudy.core.FocusLog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FocusLogService {
    private final FocusLogRepository focusLogRepository;

    public FocusLogDTO saveFocusLog(FocusLogDTO dto) {
        FocusLog focusLog = new FocusLog(
            dto.userId(),
            dto.subjectId(),
            dto.durationMs(),
            dto.startTimeMs(),
            dto.tagName()
        );
        FocusLog saved = focusLogRepository.save(focusLog);
        return convertToDTO(saved);
    }

    public List<FocusLogDTO> findFocusLogsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return focusLogRepository.findByUserIdAndDateRange(userId, start, end)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<FocusLogDTO> getFocusLogsByUser(UUID userId) {
        return focusLogRepository.findByUserId(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<FocusLogDTO> getFocusLogsBySubject(UUID subjectId) {
        return focusLogRepository.findBySubjectId(subjectId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public long getTotalFocusTimeBySubject(UUID subjectId) {
        Long total = focusLogRepository.getTotalFocusTimeBySubject(subjectId);
        return total != null ? total : 0;
    }

    public long getTotalFocusTimeByUser(UUID userId) {
        Long total = focusLogRepository.getTotalFocusTimeByUser(userId);
        return total != null ? total : 0;
    }

    private FocusLogDTO convertToDTO(FocusLog entity) {
        return new FocusLogDTO(
            entity.getId(),
            entity.getUserId(),
            entity.getSubjectId(),
            entity.getDurationMs(),
            entity.getStartTimeMs(),
            entity.getCreatedAt(),
            entity.getTagName()
        );
    }
}
