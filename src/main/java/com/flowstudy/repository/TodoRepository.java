package com.flowstudy.repository;

import com.flowstudy.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, String> {
    
    // 透過 userId 撈取該使用者的所有待辦事項
    List<Todo> findByUserId(String userId);
}