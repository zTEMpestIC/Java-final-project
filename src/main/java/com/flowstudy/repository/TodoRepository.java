package com.flowstudy.repository;

import com.flowstudy.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, String> {

    List<Todo> findByUserId(String userId);
}