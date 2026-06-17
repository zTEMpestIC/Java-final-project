package com.flowstudy.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import com.flowstudy.dto.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.UUID;

@DisplayName("Todo Service Tests")
class TodoServiceTest {
    
    private TodoRepository todoRepository;
    private TodoService todoService;
    
    @BeforeEach
    void setUp() {
        todoRepository = new InMemoryTodoRepository();
        todoService = new TodoService(todoRepository);
    }
    
    @Test
    @DisplayName("Should create todo successfully")
    void testCreateTodo() {
        UUID userId = UUID.randomUUID();
        
        TodoDTO dto = TodoDTO.create(
            userId,
            "Study Math",
            "Complete Chapter 5",
            LocalDateTime.now().plusDays(7),
            5
        );
        TodoDTO created = todoService.createTodo(dto);
        
        assertNotNull(created.id());
        assertEquals(userId, created.userId());
        assertEquals("Study Math", created.title());
        assertEquals(TodoDTO.TodoStatus.TODO, created.status());
        assertEquals(5, created.priority());
    }
    
    @Test
    @DisplayName("Should retrieve todos by user")
    void testGetTodosByUser() {
        UUID userId = UUID.randomUUID();
        
        todoService.createTodo(TodoDTO.create(userId, "Task 1", "Desc 1", LocalDateTime.now().plusDays(1), 3));
        todoService.createTodo(TodoDTO.create(userId, "Task 2", "Desc 2", LocalDateTime.now().plusDays(2), 5));
        
        var todos = todoService.getTodosByUser(userId);
        assertEquals(2, todos.size());
    }
    
    @Test
    @DisplayName("Should update todo status")
    void testUpdateTodo() {
        UUID userId = UUID.randomUUID();
        
        TodoDTO created = todoService.createTodo(
            TodoDTO.create(userId, "Task", "Desc", LocalDateTime.now().plusDays(1), 3)
        );
        
        TodoDTO updated = todoService.updateTodo(new TodoDTO(
            created.id(),
            userId,
            "Task",
            "Desc",
            TodoDTO.TodoStatus.DONE,
            created.createdAt(),
            created.dueDate(),
            3
        ));
        
        assertEquals(TodoDTO.TodoStatus.DONE, updated.status());
    }
    
    // Mock in-memory repository
    static class InMemoryTodoRepository implements TodoRepository {
        private java.util.Map<UUID, Todo> store = new java.util.HashMap<>();
        
        @Override
        public <S extends Todo> S save(S entity) {
            store.put(entity.getId(), entity);
            return entity;
        }
        
        @Override
        public java.util.Optional<Todo> findById(UUID id) {
            return java.util.Optional.ofNullable(store.get(id));
        }
        
        @Override
        public java.util.List<Todo> findByUserId(UUID userId) {
            return store.values().stream()
                .filter(t -> t.getUserId().equals(userId))
                .collect(java.util.stream.Collectors.toList());
        }
        
        @Override
        public java.util.List<Todo> findByUserIdAndStatus(UUID userId, TodoDTO.TodoStatus status) {
            return store.values().stream()
                .filter(t -> t.getUserId().equals(userId) && t.getStatus().equals(status))
                .collect(java.util.stream.Collectors.toList());
        }
        
        @Override
        public java.util.List<Todo> findByUserIdOrderByPriorityDescCreatedAtAsc(UUID userId) {
            return store.values().stream()
                .filter(t -> t.getUserId().equals(userId))
                .sorted((a, b) -> {
                    int priorityCompare = Integer.compare(b.getPriority(), a.getPriority());
                    return priorityCompare != 0 ? priorityCompare : a.getCreatedAt().compareTo(b.getCreatedAt());
                })
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Stub methods
        @Override public java.util.List<Todo> findAll() { return java.util.List.of(); }
        @Override public void deleteById(UUID id) {}
    }
}
