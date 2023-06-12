package com.example.taskmanagment.model.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class UserPassDTO {
    @NotBlank(message = "Password is required")
    private String password;
}
