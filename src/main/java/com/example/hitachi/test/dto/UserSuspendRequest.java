package com.example.hitachi.test.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSuspendRequest {
    @NotNull(message = "Suspended status cannot be null")
    private Boolean suspended;
}
