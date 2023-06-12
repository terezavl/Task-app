package com.example.taskmanagment.model.repositories;

import com.example.taskmanagment.model.entities.Task;
import com.example.taskmanagment.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findUserById(long id);
    void deleteUserById(long id);
}
