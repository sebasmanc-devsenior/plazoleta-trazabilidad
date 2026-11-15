package com.plazoleta.traceability.domain.model;

import java.time.LocalDateTime;

/** Registro de un cambio de estado de un pedido. Se valida al crearse. */
public class OrderLog {
    private final String id;
    private final Long orderId;
    private final Long clientId;
    private final Long restaurantId;
    private final Long employeeId;
    private final String previousStatus;
    private final String newStatus;
    private final LocalDateTime changedAt;

    public OrderLog(String id, Long orderId, Long clientId, Long restaurantId, Long employeeId,
                    String previousStatus, String newStatus, LocalDateTime changedAt) {
        if (orderId == null) {
            throw new IllegalArgumentException("El pedido es obligatorio");
        }
        if (clientId == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (restaurantId == null) {
            throw new IllegalArgumentException("El restaurante es obligatorio");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("El estado nuevo es obligatorio");
        }
        if (changedAt == null) {
            throw new IllegalArgumentException("La fecha del cambio es obligatoria");
        }
        this.id = id;
        this.orderId = orderId;
        this.clientId = clientId;
        this.restaurantId = restaurantId;
        this.employeeId = employeeId;
        this.previousStatus = previousStatus == null || previousStatus.isBlank() ? null : previousStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
    }

    public String getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getClientId() { return clientId; }
    public Long getRestaurantId() { return restaurantId; }
    public Long getEmployeeId() { return employeeId; }
    public String getPreviousStatus() { return previousStatus; }
    public String getNewStatus() { return newStatus; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
