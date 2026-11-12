package com.plazoleta.traceability.application.dto;

import java.time.LocalDateTime;

import com.plazoleta.traceability.domain.model.OrderLog;

public class OrderLogResponse {
    private final Long orderId;
    private final Long clientId;
    private final Long restaurantId;
    private final Long employeeId;
    private final String previousStatus;
    private final String newStatus;
    private final LocalDateTime changedAt;

    public OrderLogResponse(Long orderId, Long clientId, Long restaurantId, Long employeeId, String previousStatus,
                            String newStatus, LocalDateTime changedAt) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.restaurantId = restaurantId;
        this.employeeId = employeeId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
    }

    public static OrderLogResponse from(OrderLog log) {
        return new OrderLogResponse(log.getOrderId(), log.getClientId(), log.getRestaurantId(), log.getEmployeeId(),
                log.getPreviousStatus(), log.getNewStatus(), log.getChangedAt());
    }

    public Long getOrderId() { return orderId; }
    public Long getClientId() { return clientId; }
    public Long getRestaurantId() { return restaurantId; }
    public Long getEmployeeId() { return employeeId; }
    public String getPreviousStatus() { return previousStatus; }
    public String getNewStatus() { return newStatus; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
