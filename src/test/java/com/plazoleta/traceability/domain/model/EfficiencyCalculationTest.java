package com.plazoleta.traceability.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/** HU-18: cálculo de la eficiencia de los pedidos y del ranking de empleados. */
class EfficiencyCalculationTest {
    private static final LocalDateTime T0 = LocalDateTime.of(2026, 11, 13, 12, 0);

    private static OrderLog log(long orderId, Long employeeId, String previous, String next, int minute) {
        return new OrderLog(null, orderId, 7L, 1L, employeeId, previous, next, T0.plusMinutes(minute));
    }

    private static List<OrderLog> deliveredOrder(long orderId, long employeeId, int deliveredAtMinute) {
        return List.of(log(orderId, null, null, "PENDIENTE", 0),
                log(orderId, employeeId, "PENDIENTE", "EN_PREPARACION", 1),
                log(orderId, employeeId, "EN_PREPARACION", "LISTO", deliveredAtMinute - 1),
                log(orderId, employeeId, "LISTO", "ENTREGADO", deliveredAtMinute));
    }

    @Test
    void measuresFromPendingUntilDelivered() {
        OrderEfficiency order = OrderEfficiency.fromLogs(deliveredOrder(5, 9, 25)).orElseThrow();

        assertThat(order.getOrderId()).isEqualTo(5L);
        assertThat(order.getEmployeeId()).isEqualTo(9L);
        assertThat(order.getStartedAt()).isEqualTo(T0);
        assertThat(order.getFinishedAt()).isEqualTo(T0.plusMinutes(25));
        assertThat(order.getDuration()).isEqualTo(Duration.ofMinutes(25));
    }

    @Test
    void cancelledOrdersAreNotCounted() {
        assertThat(OrderEfficiency.fromLogs(List.of(log(5, null, null, "PENDIENTE", 0),
                log(5, null, "PENDIENTE", "CANCELADO", 3)))).isEmpty();
    }

    @Test
    void ordersStillInProgressAreNotCounted() {
        assertThat(OrderEfficiency.fromLogs(List.of(log(5, null, null, "PENDIENTE", 0),
                log(5, 9L, "PENDIENTE", "EN_PREPARACION", 2), log(5, 9L, "EN_PREPARACION", "LISTO", 9)))).isEmpty();
    }

    @Test
    void anOrderWhoseStartWasNotRecordedIsNotCounted() {
        assertThat(OrderEfficiency.fromLogs(List.of(log(5, 9L, "LISTO", "ENTREGADO", 9)))).isEmpty();
    }

    @Test
    void anOrderCannotFinishBeforeItStarts() {
        assertThatThrownBy(() -> new OrderEfficiency(5L, 9L, T0, T0.minusMinutes(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void ranksEmployeesFromTheLowestToTheHighestAverageTime() {
        List<OrderEfficiency> orders = List.of(
                OrderEfficiency.fromLogs(deliveredOrder(1, 9, 20)).orElseThrow(),
                OrderEfficiency.fromLogs(deliveredOrder(2, 9, 40)).orElseThrow(),
                OrderEfficiency.fromLogs(deliveredOrder(3, 4, 15)).orElseThrow());

        List<EmployeeEfficiency> ranking = EmployeeEfficiency.rank(orders);

        assertThat(ranking).extracting(EmployeeEfficiency::getEmployeeId).containsExactly(4L, 9L);
        assertThat(ranking.get(0).getDeliveredOrders()).isEqualTo(1);
        assertThat(ranking.get(0).getAverageDuration()).isEqualTo(Duration.ofMinutes(15));
        assertThat(ranking.get(1).getDeliveredOrders()).isEqualTo(2);
        assertThat(ranking.get(1).getAverageDuration()).isEqualTo(Duration.ofMinutes(30));
    }

    @Test
    void aTieIsBrokenByTheEmployeeId() {
        List<EmployeeEfficiency> ranking = EmployeeEfficiency.rank(List.of(
                OrderEfficiency.fromLogs(deliveredOrder(1, 9, 20)).orElseThrow(),
                OrderEfficiency.fromLogs(deliveredOrder(2, 4, 20)).orElseThrow()));

        assertThat(ranking).extracting(EmployeeEfficiency::getEmployeeId).containsExactly(4L, 9L);
    }
}
