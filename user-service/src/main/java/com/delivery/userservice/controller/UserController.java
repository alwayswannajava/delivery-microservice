package com.delivery.userservice.controller;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.dto.request.LoginUserRequest;
import com.delivery.userservice.dto.response.LoginResponse;
import com.delivery.userservice.dto.response.UserResponse;
import com.delivery.userservice.service.KeycloakService;
import com.delivery.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final KeycloakService keycloakService;

    public UserController(UserService userService, KeycloakService keycloakService) {
        this.userService = userService;
        this.keycloakService = keycloakService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        userService.create(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginUserRequest loginUserRequest) {
        return ResponseEntity.ok(keycloakService.login(loginUserRequest));
    }

    @PostMapping("/users/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getOrCreateCurrentUser(jwt));
    }
}
