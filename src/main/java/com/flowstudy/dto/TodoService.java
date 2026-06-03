package com.flowstudy.dto;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.flowstudy.core.Todo;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoDTO createTodo(TodoDTO dto) {
        Todo todo = new Todo(
            dto.userId(),
            dto.title(),
            dto.description(),
            dto.dueDate(),
            dto.priority()
        );
        Todo saved = todoRepository.save(todo);
        return saved.toDTO();
    }

    public TodoDTO updateTodo(TodoDTO dto) {
        Todo todo = todoRepository.findById(dto.id())
            .orElseThrow(() -> new IllegalArgumentException("Todo not found: " + dto.id()));
        
        todo.setTitle(dto.title());
        todo.setDescription(dto.description());
        todo.setStatus(dto.status());
        todo.setPriority(dto.priority());
        todo.setDueDate(dto.dueDate());
        
        Todo updated = todoRepository.save(todo);
        return updated.toDTO();
    }

    public List<TodoDTO> getTodosByUser(UUID userId) {
        return todoRepository.findByUserIdOrderByPriorityDescCreatedAtAsc(userId)
            .stream()
            .map(Todo::toDTO)
            .collect(Collectors.toList());
    }

    public List<TodoDTO> getTodosByUserAndStatus(UUID userId, TodoDTO.TodoStatus status) {
        return todoRepository.findByUserIdAndStatus(userId, status)
            .stream()
            .map(Todo::toDTO)
            .collect(Collectors.toList());
    }

    public TodoDTO getTodoById(UUID id) {
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Todo not found: " + id));
        return todo.toDTO();
    }

    public void deleteTodo(UUID id) {
        todoRepository.deleteById(id);
    }
}
