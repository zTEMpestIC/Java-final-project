package com.flowstudy.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flowstudy.core.Todo;
import java.util.List;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {
    List<Todo> findByUserId(UUID userId);

    List<Todo> findByUserIdAndStatus(UUID userId, TodoDTO.TodoStatus status);

    List<Todo> findByUserIdOrderByPriorityDescCreatedAtAsc(UUID userId);
}
