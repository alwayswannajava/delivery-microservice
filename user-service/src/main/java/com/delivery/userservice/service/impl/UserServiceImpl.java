package com.delivery.userservice.service.impl;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.entity.User;
import com.delivery.userservice.exception.DuplicateUserException;
import com.delivery.userservice.mapper.UserMapper;
import com.delivery.userservice.repository.UserRepository;
import com.delivery.userservice.service.KeycloakService;
import com.delivery.userservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakService keycloakService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.keycloakService = keycloakService;
    }

    @Override
    @Transactional
    public void create(CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.email())) {
            throw new DuplicateUserException("User with email %s already exists".formatted(createUserRequest.email()));
        }

        String keycloakUserId = keycloakService.createUser(createUserRequest);

        User user = userMapper.toUser(createUserRequest);
        user.setKeycloakId(keycloakUserId);

        userRepository.save(user);
    }

}
