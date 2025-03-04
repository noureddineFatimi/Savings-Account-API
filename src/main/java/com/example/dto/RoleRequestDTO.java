package com.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleRequestDTO {
    @NotNull(message = "Role name is null")
    private String name;
}
