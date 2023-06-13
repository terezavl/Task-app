package com.example.taskmanagment.model.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

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
    @Range(min = 1, max = 3, message = "Priority must be between 1 and 3")
    private Integer priority;
}
