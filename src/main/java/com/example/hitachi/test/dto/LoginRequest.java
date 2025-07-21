package com.example.hitachi.test.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username or email cannot be empty")
    @Schema(example = "john doe or john.doe@example.com")
    private String usernameOrEmail;

    @NotBlank(message = "Password cannot be empty")
    @Schema(example = "password123")
    private String password;
}
