package com.delivery.userservice.controller;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.service.UserService;
import com.delivery.userservice.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/new")
    public ResponseEntity<Void> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        log.info("------------------------POST REQUEST------------------------");
        userService.create(createUserRequest);
        log.info("------------------------POST REQUEST END------------------------");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
