package com.example.taskmanagment.model.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    @NotBlank(message = "User name is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z\\d]{3,}$",
            message = "User name must have at least 3 characters and 2 letters")
    private String userName;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+,.?\":{}|<>])(?=\\S+$).{8,}$",
            message = "Password must contain at least one letter, one number, and one special character")
    private String password;
    @NotBlank(message = "Please confirm password")
    private String confirmPassword;
}
