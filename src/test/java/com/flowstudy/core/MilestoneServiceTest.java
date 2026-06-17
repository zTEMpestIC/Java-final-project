package com.flowstudy.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import com.flowstudy.dto.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.UUID;

@DisplayName("Milestone Service Tests")
class MilestoneServiceTest {
    
    private MilestoneRepository milestoneRepository;
    private MilestoneService milestoneService;
    
    @BeforeEach
    void setUp() {
        milestoneRepository = new InMemoryMilestoneRepository();
        milestoneService = new MilestoneService(milestoneRepository);
    }
    
    @Test
    @DisplayName("Should create milestone successfully")
    void testCreateMilestone() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        
        MilestoneDTO dto = MilestoneDTO.create(
            userId,
            subjectId,
            "Complete Book",
            500.0,
            LocalDateTime.now().plusMonths(1),
            5
        );
        MilestoneDTO created = milestoneService.createMilestone(dto);
        
        assertNotNull(created.id());
        assertEquals(userId, created.userId());
        assertEquals(subjectId, created.subjectId());
        assertEquals("Complete Book", created.title());
        assertEquals(500.0, created.targetProgress());
        assertEquals(0.0, created.currentProgress());
        assertEquals("ON_TRACK", created.status());
    }
    
    @Test
    @DisplayName("Should update milestone progress")
    void testUpdateMilestoneProgress() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        
        MilestoneDTO created = milestoneService.createMilestone(
            MilestoneDTO.create(userId, subjectId, "Book", 500.0, LocalDateTime.now().plusMonths(1), 3)
        );
        
        milestoneService.updateMilestoneProgress(created.id(), 100.0);
        MilestoneDTO updated = milestoneService.getMilestoneById(created.id());
        
        assertEquals(100.0, updated.currentProgress());
        assertEquals("ON_TRACK", updated.status());
    }
    
    @Test
    @DisplayName("Should mark milestone as AT_RISK when progress is low")
    void testMilestoneAtRisk() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        
        MilestoneDTO created = milestoneService.createMilestone(
            MilestoneDTO.create(userId, subjectId, "Book", 500.0, LocalDateTime.now().plusMonths(1), 3)
        );
        
        milestoneService.updateMilestoneProgress(created.id(), 50.0);
        MilestoneDTO updated = milestoneService.getMilestoneById(created.id());
        
        assertEquals(50.0, updated.currentProgress());
        assertEquals("AT_RISK", updated.status());
    }
    
    // Mock in-memory repository
    static class InMemoryMilestoneRepository implements MilestoneRepository {
        private java.util.Map<UUID, Milestone> store = new java.util.HashMap<>();
        
        @Override
        public <S extends Milestone> S save(S entity) {
            store.put(entity.getId(), entity);
            return entity;
        }
        
        @Override
        public java.util.Optional<Milestone> findById(UUID id) {
            return java.util.Optional.ofNullable(store.get(id));
        }
        
        @Override
        public java.util.List<Milestone> findByUserId(UUID userId) {
            return store.values().stream()
                .filter(m -> m.getUserId().equals(userId))
                .collect(java.util.stream.Collectors.toList());
        }
        
        @Override
        public java.util.List<Milestone> findByUserIdAndStatus(UUID userId, String status) {
            return store.values().stream()
                .filter(m -> m.getUserId().equals(userId) && m.getStatus().equals(status))
                .collect(java.util.stream.Collectors.toList());
        }
        
        @Override
        public java.util.List<Milestone> findBySubjectId(UUID subjectId) {
            return store.values().stream()
                .filter(m -> m.getSubjectId().equals(subjectId))
                .collect(java.util.stream.Collectors.toList());
        }
        
        @Override
        public java.util.List<Milestone> findByUserIdAndSubjectId(UUID userId, UUID subjectId) {
            return store.values().stream()
                .filter(m -> m.getUserId().equals(userId) && m.getSubjectId().equals(subjectId))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Stub methods
        @Override public java.util.List<Milestone> findAll() { return java.util.List.of(); }
        @Override public void deleteById(UUID id) {}
    }
}
