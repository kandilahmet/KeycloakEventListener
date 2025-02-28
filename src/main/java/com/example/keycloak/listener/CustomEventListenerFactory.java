package com.example.keycloak.listener;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class CustomEventListenerFactory implements EventListenerProviderFactory {
    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new CustomEventListener();
    }

    @Override
    public void init(Config.Scope scope) {
        // Initialization logic if needed
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        // Post initialization logic if needed
    }

    @Override
    public void close() {
        // Cleanup logic if needed
    }

    @Override
    public String getId() {
        return "custom_event_listener";
    }
} 