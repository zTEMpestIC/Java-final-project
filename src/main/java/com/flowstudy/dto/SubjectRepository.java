package com.flowstudy.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flowstudy.core.Subject;
import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    List<Subject> findByUserId(UUID userId);

    List<Subject> findByUserIdAndName(UUID userId, String name);
}
