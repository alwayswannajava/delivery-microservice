package com.delivery.userservice.service.impl;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.dto.request.LoginUserRequest;
import com.delivery.userservice.dto.response.LoginResponse;
import com.delivery.userservice.exception.InvalidLoginException;
import com.delivery.userservice.exception.KeycloakRegistrationException;
import com.delivery.userservice.service.KeycloakService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeycloakServiceImpl implements KeycloakService {
    private final Keycloak keycloak;

    @Value("${spring.keycloak.server-url}")
    private String serverUrl;

    @Value("${spring.keycloak.realm}")
    private String realm;

    @Value("${spring.keycloak.client-id}")
    private String clientId;

    public KeycloakServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public String createUser(CreateUserRequest createUserRequest) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(createUserRequest.email());
        user.setEmail(createUserRequest.email());
        user.setFirstName(createUserRequest.name());
        user.setEnabled(true);

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(createUserRequest.password());
        cred.setTemporary(false);
        user.setCredentials(Collections.singletonList(cred));

        try (Response response = keycloak.realm(realm).users().create(user)) {
            int status = response.getStatus();

            if (status == Response.Status.CREATED.getStatusCode()) {
                return CreatedResponseUtil.getCreatedId(response);
            }

            throw new KeycloakRegistrationException("Failed to create user in Keycloak. Status: " + status);
        }
    }

    @Override
    public LoginResponse login(LoginUserRequest loginUserRequest) {
        try (Keycloak userKeycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .username(loginUserRequest.email())
                .password(loginUserRequest.password())
                .grantType("password")
                .build()) {
            AccessTokenResponse tokenResponse = userKeycloak.tokenManager().getAccessToken();

            return new LoginResponse(
                    tokenResponse.getToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getTokenType(),
                    tokenResponse.getExpiresIn(),
                    tokenResponse.getRefreshExpiresIn()
            );
        } catch (BadRequestException | NotAuthorizedException exception) {
            throw new InvalidLoginException("Invalid email or password");
        }
    }

}
