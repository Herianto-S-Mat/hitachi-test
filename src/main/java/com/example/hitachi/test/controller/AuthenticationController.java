package com.example.hitachi.test.controller;

import com.example.hitachi.test.dto.ApiResponse;
import com.example.hitachi.test.dto.JwtAuthResponse;
import com.example.hitachi.test.dto.LoginRequest;
import com.example.hitachi.test.dto.RegisterRequest;
import com.example.hitachi.test.dto.UserResponse;
import com.example.hitachi.test.entity.User;
import com.example.hitachi.test.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.example.hitachi.test.exception.UserAlreadyExistsException;

import org.springframework.security.core.context.SecurityContextHolder;
import com.example.hitachi.test.entity.Role;
import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User registeredUser = authenticationService.register(request);
        System.out.println("success");
        UserResponse userResponse = new UserResponse(
                registeredUser.getId(),
                registeredUser.getUsername(),
                registeredUser.getEmail(),
                registeredUser.isSuspended(),
                registeredUser.getRoles(),
                registeredUser.getCreatedAt(),
                registeredUser.getUpdatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        JwtAuthResponse jwtAuthResponse = authenticationService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", jwtAuthResponse));
    }


    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal User user) {
        // Get roles from the SecurityContextHolder, which includes dynamically added roles
        Set<Role> currentRoles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(authority -> {
                    Role role = new Role();
                    role.setName(authority.getAuthority());
                    // Note: Role ID is not available from GrantedAuthority, so it will be null or default
                    System.out.println("aaaaaaaaaaaaaaaa  bbbbbbbbb" +role.getName());
                    return role;
                })
                .collect(Collectors.toSet());

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isSuspended(),
                currentRoles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "User data retrieved successfully", userResponse));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Invalid username/email or password", null));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "User not found", null));
    }
}
