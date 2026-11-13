package com.plazoleta.traceability.application.dto;

import java.time.LocalDateTime;

import com.plazoleta.traceability.domain.model.OrderEfficiency;

/** HU-18: tiempo de un pedido entregado. */
public class OrderEfficiencyResponse {
    private final Long orderId;
    private final Long employeeId;
    private final LocalDateTime startedAt;
    private final LocalDateTime finishedAt;
    private final long durationSeconds;
    private final String duration;

    public OrderEfficiencyResponse(Long orderId, Long employeeId, LocalDateTime startedAt, LocalDateTime finishedAt,
                                   long durationSeconds, String duration) {
        this.orderId = orderId;
        this.employeeId = employeeId;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.durationSeconds = durationSeconds;
        this.duration = duration;
    }

    public static OrderEfficiencyResponse from(OrderEfficiency order) {
        long seconds = order.getDuration().toSeconds();
        return new OrderEfficiencyResponse(order.getOrderId(), order.getEmployeeId(), order.getStartedAt(),
                order.getFinishedAt(), seconds, DurationFormat.hhmmss(seconds));
    }

    public Long getOrderId() { return orderId; }
    public Long getEmployeeId() { return employeeId; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public long getDurationSeconds() { return durationSeconds; }
    public String getDuration() { return duration; }
}
