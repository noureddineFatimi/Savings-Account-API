package com.example.service;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.dto.UserRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.Exceptions.GlobalException.AlreadyExistException;
import com.example.Exceptions.GlobalException.ResourceNotFoundException;
import com.example.Exceptions.GlobalException.RoleNotFoundException;
import com.example.Exceptions.GlobalException.PasswordLenghtException;;

@Service
public class UserService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(UserRequestDTO userDTO) {
        User user = new User();
        
        if(userDTO.getUsername() == null || userDTO.getUsername().isBlank() || userRepository.existsByUsername(userDTO.getUsername())){
            throw new AlreadyExistException("User already exists with this username or the username passed is blank");
        }

        user.setFullName(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Set<Role> roles = new HashSet<>();
        for (String roleName : userDTO.getRoles()) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new RoleNotFoundException("Le role : " + roleName + " n'éxiste pas dans la base de données");
            }
            roles.add(role);
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public User updateUser(Integer id, UserRequestDTO userRequestDTO) {

        Optional<User> optionalUser = userRepository.findById(id);

        if(!optionalUser.isPresent()){
            throw new ResourceNotFoundException("User dosn't exists with this id");
        }

        User updatedUser = optionalUser.get();

        if(userRequestDTO.getFullName() != null && !userRequestDTO.getFullName().isBlank()) {
            updatedUser.setFullName(userRequestDTO.getFullName());
        }

        String userName = userRequestDTO.getUsername();

        if(userName != null && !userName.isBlank()) {

            if(userRepository.existsByUsername(userName)){
                throw new AlreadyExistException("User already exists with this username");
            }

            updatedUser.setUsername(userName);

        }

        String password = userRequestDTO.getPassword();

        if(password!= null && !password.isBlank()) {

            if(password.length() < 8){
                throw new PasswordLenghtException("The password must be at least 8 characters long.");
            }

            updatedUser.setPassword(passwordEncoder.encode(password));

        }
        
        Set<String> roles = userRequestDTO.getRoles();

        if(roles != null && !roles.isEmpty()) {

            Set<Role> setroles = new HashSet<>();
            for (String roleName : roles) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new RoleNotFoundException("Le role :" + roleName + " n' éxiste pas dans la base de données");
            }
            setroles.add(role);
        }

        updatedUser.setRoles(setroles);

        }

        return userRepository.save(updatedUser);

    }

    public User addRolesToUser(Integer id, Set<String> roles) {

        Optional<User> optionalUser = userRepository.findById(id);

        if(!optionalUser.isPresent()){
            throw new ResourceNotFoundException("User dosn't exists with this id");
        }

        User updatedUser = optionalUser.get();

        Set<Role> setRoles = updatedUser.getRoles();

        for (String roleName : roles) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new RoleNotFoundException("Le role : " + roleName + " n' éxiste pas");
            }
            if(setRoles.contains(role)) {
                continue;
            }
            setRoles.add(role);
        }
        updatedUser.setRoles(setRoles);

        return userRepository.save(updatedUser);

    }


    public User deleteRolesToUser(Integer id, Set<String> roles) {

        Optional<User> optionalUser = userRepository.findById(id);

        if(!optionalUser.isPresent()){
            throw new ResourceNotFoundException("User dosn't exists with this id");
        }

        User updatedUser = optionalUser.get();

        Set<Role> setRoles = updatedUser.getRoles();

        for (String roleName : roles) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new RoleNotFoundException("Le role : " + roleName + " n' éxiste pas");
            }
            if(setRoles.contains(role)) {
                throw new RoleNotFoundException("Le User " + updatedUser.getFullName() + " n'a pas l'autorité"+ roleName +" déjà");
            }
            setRoles.remove(role);
        }
        updatedUser.setRoles(setRoles);

        return userRepository.save(updatedUser);

    }

    public User getUserByUsername(String UserName) {
        if(userRepository.existsByUsername(UserName)){
            User User = userRepository.findByUsername(UserName);
            return User;
        }
        else{
            return null;
        }
    }

    public User getUserById(Integer id) {

        Optional<User> optionnalUser = userRepository.findById(id);
        if(optionnalUser.isPresent()){
            return optionnalUser.get();
        }
        else{
            return null;
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User deleteUserById(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if(!optionalUser.isPresent()){
            throw new ResourceNotFoundException("User dosn't exists with this id");
        }
        User user = optionalUser.get();

        userRepository.delete(user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
             Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map((roles) -> new SimpleGrantedAuthority(roles.getName()))
                .collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                authorities
        );
    }

}
