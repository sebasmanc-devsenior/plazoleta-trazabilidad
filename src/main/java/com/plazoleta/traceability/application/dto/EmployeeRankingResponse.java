package com.plazoleta.traceability.application.dto;

import com.plazoleta.traceability.domain.model.EmployeeEfficiency;

/** HU-18: un puesto del ranking de empleados por tiempo medio. */
public class EmployeeRankingResponse {
    private final int position;
    private final Long employeeId;
    private final int deliveredOrders;
    private final long averageDurationSeconds;
    private final String averageDuration;

    public EmployeeRankingResponse(int position, Long employeeId, int deliveredOrders, long averageDurationSeconds,
                                   String averageDuration) {
        this.position = position;
        this.employeeId = employeeId;
        this.deliveredOrders = deliveredOrders;
        this.averageDurationSeconds = averageDurationSeconds;
        this.averageDuration = averageDuration;
    }

    public static EmployeeRankingResponse from(int position, EmployeeEfficiency employee) {
        long seconds = employee.getAverageDuration().toSeconds();
        return new EmployeeRankingResponse(position, employee.getEmployeeId(), employee.getDeliveredOrders(),
                seconds, DurationFormat.hhmmss(seconds));
    }

    public int getPosition() { return position; }
    public Long getEmployeeId() { return employeeId; }
    public int getDeliveredOrders() { return deliveredOrders; }
    public long getAverageDurationSeconds() { return averageDurationSeconds; }
    public String getAverageDuration() { return averageDuration; }
}
