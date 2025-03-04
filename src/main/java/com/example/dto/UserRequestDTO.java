package com.example.dto;

import lombok.Data;
import java.util.Set;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class UserRequestDTO {
    @NotNull(message = "Fullname input is null")
    private String fullName;

    @NotNull(message = "Username input is null")
    private String username;

    @Size(min = 8, message = "The password must be at least 8 characters long. ")
    @NotNull(message = "Password input is null")
    private String password;

    private Set<String> roles; // Set pour les roles
}