package com.example.taskmanagment.model.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditUserDTO {
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z\\d]{3,}$",
            message = "User name must have at least 3 characters and 2 letters")
    private String userName;
    @Email(message = "Invalid email")
    private String email;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+,.?\":{}|<>])(?=\\S+$).{8,}$",
            message = "Password must contain at least one letter, one number, and one special character")
    private String password;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+,.?\":{}|<>])(?=\\S+$).{8,}$",
            message = "Password must contain at least one letter, one number, and one special character")
    private String confirmPassword;
}
