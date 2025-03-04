package com.example.dto;

import java.util.Set;

import com.example.model.Role;
import com.example.model.User;
import lombok.Data;

@Data
public class RoleResponseDTO {
    private Role role;
    private Set<User> users;
}
