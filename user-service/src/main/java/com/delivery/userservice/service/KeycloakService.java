package com.delivery.userservice.service;


import com.delivery.userservice.dto.request.CreateUserRequest;

public interface KeycloakService {

    String createUser(CreateUserRequest createUserRequest);

}
