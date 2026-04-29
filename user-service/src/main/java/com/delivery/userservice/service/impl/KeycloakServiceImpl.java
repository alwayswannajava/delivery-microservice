package com.delivery.userservice.service.impl;

import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.exception.DuplicateUserException;
import com.delivery.userservice.exception.KeycloakRegistrationException;
import com.delivery.userservice.service.KeycloakService;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeycloakServiceImpl implements KeycloakService {
    private final Keycloak keycloak;

    @Value("${spring.keycloak.realm}")
    private String realm;

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

}
