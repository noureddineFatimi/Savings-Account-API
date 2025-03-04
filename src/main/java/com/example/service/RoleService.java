package com.example.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.dto.RoleRequestDTO;
import com.example.model.Role;
import com.example.repository.RoleRepository;
import com.example.Exceptions.GlobalException.AlreadyExistException;
import com.example.Exceptions.GlobalException.ResourceNotFoundException;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Role createRole(RoleRequestDTO roleDTO) {
        String roleName = roleDTO.getName();
        if(roleName == null || roleName.isBlank() || roleRepository.existsByName(roleName)) {
            throw new AlreadyExistException("Role already exist or the role name passed is blank");
        }
        Role role = new Role();
        role.setName(roleDTO.getName());
        return roleRepository.save(role);
    }

    public Role updateRoleById(Integer id, String roleName) {
        Optional<Role> optionalrole = roleRepository.findById(id);
        if(!optionalrole.isPresent()) {
            throw new ResourceNotFoundException("Role dosn't exist with this id");
        }
        Role role = optionalrole.get();
        if(roleName != null && !roleName.isBlank()) {
            if(roleRepository.existsByName(roleName)) {
            throw new AlreadyExistException("Role already exist");
            }
            role.setName(roleName);
        }
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role deleteRoleById(Integer id) {
        Optional<Role> optionalRole = roleRepository.findById(id);

        if(!optionalRole.isPresent()){
            throw new ResourceNotFoundException("Role dosn't exists with this id ");
        }
        Role role = optionalRole.get();

        roleRepository.delete(role);
        return role;
    }
    
}
