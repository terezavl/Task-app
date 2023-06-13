package com.example.taskmanagment.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfoDTO {
    private long id;
    private UserWithoutPassDTO user;
    private String title;
    private String description;
    private Boolean isFinished;
    private int priority;
}
