package com.plazoleta.traceability.application.dto;

/** Datos que envía el microservicio de restaurantes cada vez que un pedido cambia de estado. */
public class RecordOrderLogCommand {
    private Long orderId;
    private Long clientId;
    private Long restaurantId;
    private Long employeeId;
    private String previousStatus;
    private String newStatus;

    public RecordOrderLogCommand() {}

    public RecordOrderLogCommand(Long orderId, Long clientId, Long restaurantId, Long employeeId,
                                 String previousStatus, String newStatus) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.restaurantId = restaurantId;
        this.employeeId = employeeId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
}
