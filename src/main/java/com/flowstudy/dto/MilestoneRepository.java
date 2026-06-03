package com.flowstudy.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flowstudy.core.Milestone;
import java.util.List;
import java.util.UUID;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, UUID> {
    List<Milestone> findByUserId(UUID userId);

    List<Milestone> findByUserIdAndStatus(UUID userId, String status);

    List<Milestone> findBySubjectId(UUID subjectId);

    List<Milestone> findByUserIdAndSubjectId(UUID userId, UUID subjectId);
}
