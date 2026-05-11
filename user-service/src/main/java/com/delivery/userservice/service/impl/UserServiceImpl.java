package com.delivery.userservice.service.impl;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.dto.response.UserResponse;
import com.delivery.userservice.entity.User;
import com.delivery.userservice.exception.DuplicateUserException;
import com.delivery.userservice.exception.InvalidOAuth2UserException;
import com.delivery.userservice.mapper.UserMapper;
import com.delivery.userservice.repository.UserRepository;
import com.delivery.userservice.service.KeycloakService;
import com.delivery.userservice.service.UserService;
import org.springframework.security.oauth2.jwt.Jwt;
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

    @Override
    @Transactional
    public UserResponse getOrCreateCurrentUser(Jwt jwt) {
        String keycloakId = requiredClaim(jwt.getSubject(), "sub");
        String email = requiredClaim(jwt.getClaimAsString("email"), "email");

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> createOAuth2User(jwt, keycloakId, email));

        return toResponse(user);
    }

    private User createOAuth2User(Jwt jwt, String keycloakId, String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateUserException("User with email %s already exists".formatted(email));
        }

        User user = new User();
        user.setName(resolveName(jwt, email));
        user.setEmail(email);
        user.setKeycloakId(keycloakId);

        return userRepository.save(user);
    }

    private String resolveName(Jwt jwt, String email) {
        String name = jwt.getClaimAsString("name");
        if (hasText(name)) {
            return name;
        }

        String preferredUsername = jwt.getClaimAsString("preferred_username");
        if (hasText(preferredUsername)) {
            return preferredUsername;
        }

        return email;
    }

    private String requiredClaim(String value, String claimName) {
        if (!hasText(value)) {
            throw new InvalidOAuth2UserException("OAuth2 token does not contain required claim: " + claimName);
        }

        return value;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

}
