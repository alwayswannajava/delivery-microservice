package com.delivery.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @Size(min = 2, max = 255, message = "Name should be between 2 and 255 characters")
        String name,

        @Email
        @Size(min = 2, max = 30, message = "Email should be between 2 and 30 characters")
        String email
) {
}
