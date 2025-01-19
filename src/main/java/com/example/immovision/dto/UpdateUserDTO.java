package com.example.immovision.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "New email is required")
    @Email(message = "Invalid new email format")
    private String newEmail;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name is too long")
    private String name;
}