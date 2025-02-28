package com.example.keycloak.listener;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CustomEventListener implements EventListenerProvider {
    private final RabbitMQManager rabbitMQManager;
    private final ObjectMapper objectMapper;

    public CustomEventListener() {
        this.rabbitMQManager = RabbitMQManager.getInstance();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onEvent(Event event) {
        // Normal events are not needed anymore as we handle sessions through AdminEvents
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        try {
            // Handle role mapping events
            if (adminEvent.getResourceType() == ResourceType.CLIENT_ROLE_MAPPING || 
                adminEvent.getResourceType() == ResourceType.REALM_ROLE_MAPPING) {
                handleRoleMappingEvent(adminEvent);
            }
            // Handle user deletion events
            else if (adminEvent.getResourceType() == ResourceType.USER && 
                    adminEvent.getOperationType() == OperationType.DELETE) {
                handleUserDeletionEvent(adminEvent);
            }
            // Handle group membership events
            else if (adminEvent.getResourceType() == ResourceType.GROUP_MEMBERSHIP) {
                handleGroupMembershipEvent(adminEvent);
            }
            // Handle user session events
            else if (adminEvent.getResourceType() == ResourceType.USER_SESSION && 
                    adminEvent.getOperationType() == OperationType.DELETE) {
                handleUserSessionEvent(adminEvent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRoleMappingEvent(AdminEvent adminEvent) throws Exception {
        ObjectNode messageJson = objectMapper.createObjectNode();
        messageJson.put("type", "ROLE_CHANGE");
        messageJson.put("operation", adminEvent.getOperationType().name());
        messageJson.put("realmId", adminEvent.getRealmId());
        messageJson.put("resourcePath", adminEvent.getResourcePath());
        messageJson.put("timestamp", adminEvent.getTime());
        messageJson.put("representation", adminEvent.getRepresentation());

        rabbitMQManager.publishMessage("user.role", messageJson.toString());
    }

    private void handleUserDeletionEvent(AdminEvent adminEvent) throws Exception {
        ObjectNode messageJson = objectMapper.createObjectNode();
        messageJson.put("type", "USER_DELETE");
        messageJson.put("realmId", adminEvent.getRealmId());
        messageJson.put("resourcePath", adminEvent.getResourcePath());
        messageJson.put("timestamp", adminEvent.getTime());

        rabbitMQManager.publishMessage("user.delete", messageJson.toString());
    }

    private void handleGroupMembershipEvent(AdminEvent adminEvent) throws Exception {
        ObjectNode messageJson = objectMapper.createObjectNode();
        messageJson.put("type", "GROUP_MEMBERSHIP");
        messageJson.put("operation", adminEvent.getOperationType().name());
        messageJson.put("realmId", adminEvent.getRealmId());
        messageJson.put("resourcePath", adminEvent.getResourcePath());
        messageJson.put("timestamp", adminEvent.getTime());
        messageJson.put("representation", adminEvent.getRepresentation());

        rabbitMQManager.publishMessage("user.group", messageJson.toString());
    }

    private void handleUserSessionEvent(AdminEvent adminEvent) throws Exception {
        ObjectNode messageJson = objectMapper.createObjectNode();
        messageJson.put("type", "USER_SESSION");
        messageJson.put("operation", adminEvent.getOperationType().name());
        messageJson.put("realmId", adminEvent.getRealmId());
        messageJson.put("resourcePath", adminEvent.getResourcePath());
        messageJson.put("timestamp", adminEvent.getTime());
        messageJson.put("representation", adminEvent.getRepresentation());

        rabbitMQManager.publishMessage("user.session", messageJson.toString());
    }

    @Override
    public void close() {
        rabbitMQManager.close();
    }
} 