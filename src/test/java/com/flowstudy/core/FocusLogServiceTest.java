package com.flowstudy.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import com.flowstudy.dto.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.UUID;

@DisplayName("FocusLog Service Tests")
class FocusLogServiceTest {
    
    private FocusLogRepository focusLogRepository;
    private FocusLogService focusLogService;
    
    @BeforeEach
    void setUp() {
        focusLogRepository = new InMemoryFocusLogRepository();
        focusLogService = new FocusLogService(focusLogRepository);
    }
    
    @Test
    @DisplayName("Should save focus log successfully")
    void testSaveFocusLog() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        
        FocusLogDTO dto = FocusLogDTO.create(userId, subjectId, 1500000, "Pomodoro");
        FocusLogDTO saved = focusLogService.saveFocusLog(dto);
        
        assertNotNull(saved.id());
        assertEquals(userId, saved.userId());
        assertEquals(subjectId, saved.subjectId());
        assertEquals(1500000, saved.durationMs());
    }
    
    @Test
    @DisplayName("Should retrieve focus logs by user")
    void testGetFocusLogsByUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        
        focusLogService.saveFocusLog(FocusLogDTO.create(userId, subjectId, 1500000, "Pomodoro"));
        focusLogService.saveFocusLog(FocusLogDTO.create(userId, subjectId, 2000000, "Self-Study"));
        
        var logs = focusLogService.getFocusLogsByUser(userId);
        assertEquals(2, logs.size());
    }
    
    @Test
    @DisplayName("Should calculate total focus time by subject")
    void testGetTotalFocusTimeBySubject() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        
        focusLogService.saveFocusLog(FocusLogDTO.create(userId, subjectId, 1500000, "Pomodoro"));
        focusLogService.saveFocusLog(FocusLogDTO.create(userId, subjectId, 1500000, "Pomodoro"));
        
        long total = focusLogService.getTotalFocusTimeBySubject(subjectId);
        assertEquals(3000000, total);
    }
    
    // Mock in-memory repository for testing
    static class InMemoryFocusLogRepository implements FocusLogRepository {
        private java.util.Map<UUID, FocusLog> store = new java.util.HashMap<>();
        
        @Override
        public <S extends FocusLog> S save(S entity) {
            store.put(entity.getId(), entity);
            return entity;
        }
        
        @Override
        public java.util.List<FocusLog> findByUserId(UUID userId) {
            return store.values().stream()
                .filter(fl -> fl.getUserId().equals(userId))
                .collect(java.util.stream.Collectors.toList());
        }
        
        @Override
        public java.util.List<FocusLog> findBySubjectId(UUID subjectId) {
            return store.values().stream()
                .filter(fl -> fl.getSubjectId().equals(subjectId))
                .collect(java.util.stream.Collectors.toList());
        }
        
        @Override
        public Long getTotalFocusTimeBySubject(UUID subjectId) {
            return store.values().stream()
                .filter(fl -> fl.getSubjectId().equals(subjectId))
                .mapToLong(FocusLog::getDurationMs)
                .sum();
        }
        
        @Override
        public Long getTotalFocusTimeByUser(UUID userId) {
            return store.values().stream()
                .filter(fl -> fl.getUserId().equals(userId))
                .mapToLong(FocusLog::getDurationMs)
                .sum();
        }
        
        // Stub methods
        @Override public java.util.Optional<FocusLog> findById(UUID id) { return java.util.Optional.empty(); }
        @Override public java.util.List<FocusLog> findAll() { return java.util.List.of(); }
        @Override public void deleteById(UUID id) {}
        @Override public java.util.List<FocusLog> findByUserIdAndDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) { return java.util.List.of(); }
    }
}
