package com.delivery.userservice.service.impl;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.mapper.UserMapper;
import com.delivery.userservice.repository.UserRepository;
import com.delivery.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public void create(CreateUserRequest createUserRequest) {
        log.info("Create user incoming :{}", createUserRequest);
        try {
            userRepository.save(userMapper.toUser(createUserRequest));
        } catch (Throwable e) {
            log.error("Can't create user with data: {}", createUserRequest);
            log.error("Throwing an exception: {}", e.getMessage());
        }
    }
}
