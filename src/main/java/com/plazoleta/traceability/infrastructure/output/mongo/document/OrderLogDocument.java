package com.plazoleta.traceability.infrastructure.output.mongo.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.plazoleta.traceability.domain.model.OrderLog;

/** HU-17: documento de la colección order_logs en MongoDB. */
@Document(collection = "order_logs")
public class OrderLogDocument {
    @Id
    private String id;
    private Long orderId;
    private Long clientId;
    private Long restaurantId;
    private Long employeeId;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime changedAt;

    public OrderLogDocument() {
    }

    public static OrderLogDocument fromDomain(OrderLog log) {
        OrderLogDocument document = new OrderLogDocument();
        document.id = log.getId();
        document.orderId = log.getOrderId();
        document.clientId = log.getClientId();
        document.restaurantId = log.getRestaurantId();
        document.employeeId = log.getEmployeeId();
        document.previousStatus = log.getPreviousStatus();
        document.newStatus = log.getNewStatus();
        document.changedAt = log.getChangedAt();
        return document;
    }

    public OrderLog toDomain() {
        return new OrderLog(id, orderId, clientId, restaurantId, employeeId, previousStatus, newStatus, changedAt);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
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
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
