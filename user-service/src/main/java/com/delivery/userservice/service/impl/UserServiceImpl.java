package com.delivery.userservice.service.impl;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.mapper.UserMapper;
import com.delivery.userservice.repository.UserRepository;
import com.delivery.userservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public void create(CreateUserRequest createUserRequest) {
        userRepository.save(userMapper.toUser(createUserRequest));
    }
}
