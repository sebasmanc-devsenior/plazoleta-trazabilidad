package com.plazoleta.traceability.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** HU-18: cuánto tardó un pedido desde que se creó (PENDIENTE) hasta que se entregó (ENTREGADO). */
public class OrderEfficiency {
    public static final String START_STATUS = "PENDIENTE";
    public static final String END_STATUS = "ENTREGADO";

    private final Long orderId;
    private final Long employeeId;
    private final LocalDateTime startedAt;
    private final LocalDateTime finishedAt;

    public OrderEfficiency(Long orderId, Long employeeId, LocalDateTime startedAt, LocalDateTime finishedAt) {
        if (orderId == null || startedAt == null || finishedAt == null) {
            throw new IllegalArgumentException("Order, start and finish are required");
        }
        if (finishedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException("An order cannot finish before it starts");
        }
        this.orderId = orderId;
        this.employeeId = employeeId;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    /**
     * Arma la eficiencia de un pedido con sus registros. Queda vacío si el pedido no tiene registro de inicio
     * o todavía no se ha entregado (en proceso o cancelado): esos pedidos no se cuentan.
     */
    public static Optional<OrderEfficiency> fromLogs(List<OrderLog> logsOfOneOrder) {
        Optional<OrderLog> start = logsOfOneOrder.stream()
                .filter(log -> START_STATUS.equals(log.getNewStatus()) && log.getPreviousStatus() == null)
                .findFirst();
        Optional<OrderLog> end = logsOfOneOrder.stream()
                .filter(log -> END_STATUS.equals(log.getNewStatus()))
                .findFirst();
        if (start.isEmpty() || end.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new OrderEfficiency(end.get().getOrderId(), end.get().getEmployeeId(),
                start.get().getChangedAt(), end.get().getChangedAt()));
    }

    public Duration getDuration() {
        return Duration.between(startedAt, finishedAt);
    }

    public Long getOrderId() { return orderId; }
    public Long getEmployeeId() { return employeeId; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
}
