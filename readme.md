> **_NOTE:_** This project was created with Cursor AI. My first experience with Cursor. Despite that, he did a very good job.
 
# Keycloak RabbitMQ Event Listener

A custom Keycloak Event Listener that publishes events to RabbitMQ.

## Features

- Listens to Keycloak events (User Session, Group Membership, Role Mapping)
- Publishes events to RabbitMQ using topic exchange
- Singleton pattern for RabbitMQ connection management

## Configuration

RabbitMQ connection settings can be configured in `RabbitMQManager.java`:
