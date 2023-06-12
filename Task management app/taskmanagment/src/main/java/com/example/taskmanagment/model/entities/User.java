package com.example.taskmanagment.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_name")
    private String userName;
    @Column
    private String email;
    @Column
    private String password;
    @OneToMany(mappedBy = "user")
    private Set<Task> tasks = new HashSet<>();
    //todo check equals and hashcode
}
