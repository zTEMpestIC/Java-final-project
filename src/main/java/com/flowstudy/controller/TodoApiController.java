package com.flowstudy.controller;


import com.flowstudy.model.Todo;
import com.flowstudy.repository.TodoRepository;
import com.flowstudy.core.contract.StudyContract.TodoDTO.TaskStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TodoApiController {

    private final TodoRepository todoRepository;

    public TodoApiController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping("/todos")
    public List<Todo> getTodos(@RequestParam String userId) {
        return todoRepository.findByUserId(userId);
    }

    // 5. 批次儲存/更新待辦事項
    @PostMapping("/todos/batch")
    @Transactional
    public Map<String, Object> saveTodosBatch(@RequestBody TodoBatchPayload payload) {
        String userIdStr = String.valueOf(payload.userId());

        // 轉換前端傳來的資料
        List<Todo> newTodos = payload.todos().stream().map(dto -> {
            // 如果資料庫中已有這個 Todo，則更新它；否則建立新的
            Todo todo = todoRepository.findById(dto.id()).orElse(new Todo());
            todo.setId(dto.id());
            todo.setTitle(dto.title());
            todo.setStatus(TaskStatus.valueOf(dto.status().toUpperCase())); // todo -> TODO
            todo.setUserId(userIdStr);
            return todo;
        }).collect(Collectors.toList());

        // 處理刪除邏輯：由於前端看板每次都會傳「完整的最新列表」過來，
        // 所以如果某個資料庫裡的 Todo 沒出現在這次的 payload 裡，代表它被前端刪除了
        List<String> payloadIds = payload.todos().stream().map(TodoDto::id).toList();
        List<Todo> existingTodos = todoRepository.findByUserId(userIdStr);
        for (Todo existing : existingTodos) {
            if (!payloadIds.contains(existing.getId())) {
                todoRepository.delete(existing);
            }
        }

        // 儲存所有更新與新增
        todoRepository.saveAll(newTodos);

        return Map.<String, Object>of("status", "success", "savedCount", newTodos.size());
    }

    // ========== DTO / Records ========== //
    public record TodoDto(String id, String title, String status) {}
    public record TodoBatchPayload(int userId, List<TodoDto> todos) {}
}
