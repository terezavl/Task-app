package com.example.taskmanagment.model.DTOs;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithoutPassDTO {
    private long id;
    private String userName;
    private String email;
}
