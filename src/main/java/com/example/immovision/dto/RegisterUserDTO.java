package com.example.immovision.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    @NotBlank(message = "Le full name est obligatoire")
    @Size(max = 50, message = "Le full name est trop long")
    private String fullName;



    @NotBlank(message = "L'adresse mail est obligatoire")
    @Email(message = "L'adresse mail est invalide")
    @Size(max = 320, message = "L'adresse mail est trop longue")
    private String email;



    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;



}
