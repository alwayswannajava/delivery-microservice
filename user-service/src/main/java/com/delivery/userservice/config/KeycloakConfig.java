package com.delivery.userservice.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${spring.keycloak.server-url}")
    private String serverUrl;

    @Value("${spring.keycloak.admin-realm}")
    private String adminRealm;

    @Value("${spring.keycloak.client-id}")
    private String clientId;

    @Value("${spring.keycloak.username}")
    private String username;

    @Value("${spring.keycloak.password}")
    private String password;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .clientId(clientId)
                .username(username)
                .password(password)
                .build();
    }
}
