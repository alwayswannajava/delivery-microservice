package com.delivery.userservice.controller;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/new")
    public ResponseEntity<Void> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        userService.create(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
