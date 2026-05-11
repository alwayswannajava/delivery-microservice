package com.delivery.userservice.service;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.dto.response.UserResponse;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {
    void create(CreateUserRequest createUserRequest);

    UserResponse getOrCreateCurrentUser(Jwt jwt);
}
