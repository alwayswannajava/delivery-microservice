package com.delivery.userservice.service;


import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.dto.request.LoginUserRequest;
import com.delivery.userservice.dto.response.LoginResponse;

public interface KeycloakService {

    String createUser(CreateUserRequest createUserRequest);

    LoginResponse login(LoginUserRequest loginUserRequest);

}
