package com.delivery.userservice.service;

import com.delivery.userservice.dto.request.CreateUserRequest;

public interface UserService {
    void create(CreateUserRequest createUserRequest);
}
