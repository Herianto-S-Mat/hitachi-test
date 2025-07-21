package com.example.hitachi.test.controller;

import com.example.hitachi.test.dto.AdminRegisterRequest;
import com.example.hitachi.test.dto.ApiResponse;
import com.example.hitachi.test.dto.UserResponse;
import com.example.hitachi.test.dto.UserSuspendRequest;
import com.example.hitachi.test.entity.User;
import com.example.hitachi.test.exception.UserAlreadyExistsException;
import com.example.hitachi.test.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createAdminUser(
            @Valid @RequestBody AdminRegisterRequest request
    ) {
        User adminUser = userService.createAdminUser(request);
        UserResponse userResponse = new UserResponse(
                adminUser.getId(),
                adminUser.getUsername(),
                adminUser.getEmail(),
                adminUser.isSuspended(),
                adminUser.getRoles(),
                adminUser.getCreatedAt(),
                adminUser.getUpdatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin user created successfully", userResponse));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponse> userResponses = users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.isSuspended(),
                        user.getRoles(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", userResponses));
    }

    @PatchMapping("/{userId}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> suspendUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserSuspendRequest request
    ) {
        User updatedUser = userService.suspendUser(userId, request.getSuspended());
        UserResponse userResponse = new UserResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.isSuspended(),
                updatedUser.getRoles(),
                updatedUser.getCreatedAt(),
                updatedUser.getUpdatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "User suspension status updated successfully", userResponse));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }
}
