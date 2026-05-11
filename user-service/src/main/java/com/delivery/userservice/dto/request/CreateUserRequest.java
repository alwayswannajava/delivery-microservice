package com.delivery.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 255, message = "Name should be between 2 and 255 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email
        @Size(min = 2, max = 255, message = "Email should be between 2 and 255 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 50, message = "Password should be between 8 and 50 characters")
        String password
) {
}
