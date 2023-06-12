package com.example.taskmanagment.model.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditTaskDTO {
    @Size(min = 1, max = 100, message = "Title can't be longer than {max} characters")
    private String title;
    @Size(max = 2000, message = "Description can't be longer than {max} characters")
    private String description;
    private Boolean isFinished;
}
