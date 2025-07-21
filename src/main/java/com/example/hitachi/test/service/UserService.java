package com.example.hitachi.test.service;

import com.example.hitachi.test.dto.AdminRegisterRequest;
import com.example.hitachi.test.entity.Role;
import com.example.hitachi.test.entity.User;
import com.example.hitachi.test.exception.UserAlreadyExistsException;
import com.example.hitachi.test.repository.RoleRepository;
import com.example.hitachi.test.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User createAdminUser(AdminRegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_ADMIN");
                    return roleRepository.save(newRole);
                });

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        User adminUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .suspended(false)
                .build();

        return userRepository.save(adminUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User suspendUser(Long userId, boolean suspended) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setSuspended(suspended);
        return userRepository.save(user);
    }
}
