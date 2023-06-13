package com.example.taskmanagment.model.repositories;

import com.example.taskmanagment.model.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
    public interface TaskRepository extends JpaRepository<Task, Integer> {
        @Query(value = "SELECT * FROM tasks WHERE user_id = :userId AND is_finished= :isFinished", nativeQuery = true)
        Page<Task> findAllByUser(long userId, Pageable pageable, int isFinished);
        @Query(value = "SELECT * FROM tasks WHERE user_id = :userId AND is_finished= :isFinished", nativeQuery = true)
        List<Task> findTasksByUser(long userId, int isFinished);
        Optional<Task> findTaskById(long id);
    }
