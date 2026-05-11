package com.delivery.userservice.service.impl;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.dto.response.UserResponse;
import com.delivery.userservice.entity.User;
import com.delivery.userservice.exception.DuplicateUserException;
import com.delivery.userservice.exception.InvalidOAuth2UserException;
import com.delivery.userservice.mapper.UserMapper;
import com.delivery.userservice.repository.UserRepository;
import com.delivery.userservice.service.KeycloakService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
    void shouldCreateKeycloakUserAndSaveLocalUser() {
        User user = new User();
        String keycloakUserId = "keycloak-user-id";

        when(userRepository.existsByEmail(REQUEST.email())).thenReturn(false);
        when(keycloakService.createUser(REQUEST)).thenReturn(keycloakUserId);
        when(userMapper.toUser(REQUEST)).thenReturn(user);

        userService.create(REQUEST);

        verify(userRepository).save(user);
    }

    @Test
    void shouldReturnExistingCurrentUserByKeycloakId() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("Google User");
        user.setEmail("google@example.com");
        user.setKeycloakId("google-keycloak-id");

        when(userRepository.findByKeycloakId(user.getKeycloakId())).thenReturn(Optional.of(user));

        UserResponse response = userService.getOrCreateCurrentUser(jwt(user.getKeycloakId(), user.getEmail(), user.getName()));

        assertEquals(userId, response.id());
        assertEquals(user.getName(), response.name());
        assertEquals(user.getEmail(), response.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCreateCurrentUserFromJwtClaims() {
        String keycloakId = "google-keycloak-id";
        String email = "google@example.com";
        String name = "Google User";
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setKeycloakId(keycloakId);
        savedUser.setEmail(email);
        savedUser.setName(name);

        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.getOrCreateCurrentUser(jwt(keycloakId, email, name));

        assertEquals(savedUser.getId(), response.id());
        assertEquals(name, response.name());
        assertEquals(email, response.email());
        verify(userRepository).save(argThat(user ->
                keycloakId.equals(user.getKeycloakId())
                        && email.equals(user.getEmail())
                        && name.equals(user.getName())));
    }

    @Test
    void shouldRejectCurrentUserWithoutEmailClaim() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("google-keycloak-id")
                .build();

        assertThrows(InvalidOAuth2UserException.class, () -> userService.getOrCreateCurrentUser(jwt));
    }

    private Jwt jwt(String subject, String email, String name) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject)
                .claim("email", email)
                .claim("name", name)
                .build();
    }
}
