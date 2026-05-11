package com.delivery.userservice.service.impl;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.entity.User;
import com.delivery.userservice.exception.DuplicateUserException;
import com.delivery.userservice.mapper.UserMapper;
import com.delivery.userservice.repository.UserRepository;
import com.delivery.userservice.service.KeycloakService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final CreateUserRequest REQUEST = new CreateUserRequest(
            "Test User",
            "test@example.com",
            "password123"
    );

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldNotCreateKeycloakUserWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(REQUEST.email())).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> userService.create(REQUEST));

        verify(keycloakService, never()).createUser(REQUEST);
    }

    @Test
    void shouldDeleteKeycloakUserWhenLocalSaveFails() {
        User user = new User();
        String keycloakUserId = "keycloak-user-id";

        when(userRepository.existsByEmail(REQUEST.email())).thenReturn(false);
        when(keycloakService.createUser(REQUEST)).thenReturn(keycloakUserId);
        when(userMapper.toUser(REQUEST)).thenReturn(user);
        when(userRepository.saveAndFlush(user)).thenThrow(new IllegalStateException("Database error"));

        assertThrows(IllegalStateException.class, () -> userService.create(REQUEST));

        verify(keycloakService).deleteUser(keycloakUserId);
    }
}
