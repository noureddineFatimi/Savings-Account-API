package com.example.controller;

import com.example.dto.UserRequestDTO;
import com.example.dto.RoleRequestDTO;
import com.example.model.User;
import com.example.model.Role;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.dto.UserResponseDTO;
import com.example.dto.RoleResponseDTO;
import com.example.service.RoleService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    
    @PostMapping("user/createUser")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userDTO) {
        User savedUser = userService.createUser(userDTO);
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUser(savedUser);
        userResponseDTO.setRoles(savedUser.getRoles());
        return ResponseEntity.ok(userResponseDTO);
    }
    
  
    @PostMapping("role/createRole")
    public ResponseEntity<Role> createUser(@Valid @RequestBody RoleRequestDTO roleDTO) {
        Role savedRole = roleService.createRole(roleDTO);
        return ResponseEntity.ok(savedRole);
    }

    @Operation(summary = "Laisser les elements non modifiés vides, null ou blancs")
    @PutMapping("updateUserById/{id}")
    public ResponseEntity<UserResponseDTO> updateUserById(@PathVariable Integer id, @RequestBody UserRequestDTO userDTO) {
        User user = userService.updateUser(id, userDTO);
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUser(user);
        userResponseDTO.setRoles(user.getRoles());
        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "Laisser les elements non modifiés vides, null ou blancs")
    @PutMapping("role/updateRoleById/{id}")
    public ResponseEntity<RoleResponseDTO> updateRoleById(@PathVariable Integer id, @RequestBody String roleName) {
        
        Role role = roleService.updateRoleById(id, roleName);
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();
        roleResponseDTO.setUsers(role.getUsers());
        roleResponseDTO.setRole(role);
        return ResponseEntity.ok(roleResponseDTO);
       
    }

    @PatchMapping("user/addRolesToUser/{id}")
    public ResponseEntity<UserResponseDTO> addRolesToUser(@Parameter(description = "Id of User") @PathVariable Integer id, @RequestBody Set<String> roles) {
        
        User user = userService.addRolesToUser(id, roles);
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUser(user);
        userResponseDTO.setRoles(user.getRoles());
        return ResponseEntity.ok(userResponseDTO);
        
        
    }

    @PatchMapping("user/deleteRolesToUser/{id}")
    public ResponseEntity<UserResponseDTO> deleteRolesToUser(@Parameter(description = "Id of User") @PathVariable Integer id, @RequestBody Set<String> roles) {
        
        User user = userService.deleteRolesToUser(id, roles);
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUser(user);
        userResponseDTO.setRoles(user.getRoles());
        return ResponseEntity.ok(userResponseDTO);
        
    }
    
    @GetMapping("user/getUserByUsername/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {

        User user = userService.getUserByUsername(username);   
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUser(user);
        userResponseDTO.setRoles(user.getRoles());
        return ResponseEntity.ok(userResponseDTO);
    }
    
    @GetMapping("user/getUserById/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {

        User user = userService.getUserById(id);
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUser(user);
        userResponseDTO.setRoles(user.getRoles());
        return ResponseEntity.ok(userResponseDTO);
    }
    
    @GetMapping("user/getAllUsers")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> listUsers = userService.getAllUsers();
        List<UserResponseDTO> list = new ArrayList<>();
        for(User user : listUsers) {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            userResponseDTO.setUser(user);
            userResponseDTO.setRoles(user.getRoles());
            list.add(userResponseDTO);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("role/getAllRoles")
    public ResponseEntity<List<Role>> getAllRoles() {

        return ResponseEntity.ok(roleService.getAllRoles());

    }

    @DeleteMapping("user/deleteUserById/{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable Integer id) {
        User deletedUser = userService.deleteUserById(id);
        return ResponseEntity.ok(deletedUser);
    }

    @DeleteMapping("role/deleteRoleById/{id}")
    public ResponseEntity<Role> deleteUsRolerById(@PathVariable Integer id) {
        Role deletedRole = roleService.deleteRoleById(id);
        return ResponseEntity.ok(deletedRole);
    }
    
    
}
