package com.plazoleta.traceability.domain.model;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** HU-18: tiempo medio de los pedidos que entregó un empleado. */
public class EmployeeEfficiency {
    private final Long employeeId;
    private final int deliveredOrders;
    private final Duration averageDuration;

    public EmployeeEfficiency(Long employeeId, int deliveredOrders, Duration averageDuration) {
        this.employeeId = employeeId;
        this.deliveredOrders = deliveredOrders;
        this.averageDuration = averageDuration;
    }

    /** Ranking: del empleado con menor tiempo medio al de mayor tiempo medio. */
    public static List<EmployeeEfficiency> rank(List<OrderEfficiency> orders) {
        Map<Long, List<OrderEfficiency>> byEmployee = orders.stream()
                .filter(order -> order.getEmployeeId() != null)
                .collect(Collectors.groupingBy(OrderEfficiency::getEmployeeId));
        return byEmployee.entrySet().stream()
                .map(entry -> new EmployeeEfficiency(entry.getKey(), entry.getValue().size(),
                        average(entry.getValue())))
                .sorted(Comparator.comparing(EmployeeEfficiency::getAverageDuration)
                        .thenComparing(EmployeeEfficiency::getEmployeeId))
                .toList();
    }

    private static Duration average(List<OrderEfficiency> orders) {
        long totalMillis = orders.stream().mapToLong(order -> order.getDuration().toMillis()).sum();
        return Duration.ofMillis(totalMillis / orders.size());
    }

    public Long getEmployeeId() { return employeeId; }
    public int getDeliveredOrders() { return deliveredOrders; }
    public Duration getAverageDuration() { return averageDuration; }
}
