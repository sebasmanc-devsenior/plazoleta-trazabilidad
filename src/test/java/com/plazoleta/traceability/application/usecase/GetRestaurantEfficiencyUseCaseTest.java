package com.plazoleta.traceability.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.traceability.application.dto.EmployeeRankingResponse;
import com.plazoleta.traceability.application.dto.OrderEfficiencyResponse;
import com.plazoleta.traceability.application.dto.RestaurantEfficiencyResponse;
import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.application.port.output.RestaurantDirectoryPort;
import com.plazoleta.traceability.domain.exception.ForbiddenOperationException;
import com.plazoleta.traceability.domain.exception.RestaurantNotFoundException;
import com.plazoleta.traceability.domain.model.OrderLog;

/** HU-18: Consultar la eficiencia de los pedidos. */
@ExtendWith(MockitoExtension.class)
class GetRestaurantEfficiencyUseCaseTest {
    private static final Long RESTAURANT_ID = 1L;
    private static final Long OWNER_ID = 2L;
    private static final LocalDateTime T0 = LocalDateTime.of(2026, 11, 13, 12, 0);

    @Mock
    private OrderLogRepositoryPort orderLogRepositoryPort;
    @Mock
    private RestaurantDirectoryPort restaurantDirectoryPort;
    @InjectMocks
    private GetRestaurantEfficiencyUseCase service;

    private static OrderLog log(long orderId, Long employeeId, String previous, String next, int seconds) {
        return new OrderLog(null, orderId, 7L, RESTAURANT_ID, employeeId, previous, next, T0.plusSeconds(seconds));
    }

    private static List<OrderLog> delivered(long orderId, long employeeId, int startSecond, int seconds) {
        return List.of(log(orderId, null, null, "PENDIENTE", startSecond),
                log(orderId, employeeId, "PENDIENTE", "EN_PREPARACION", startSecond + 1),
                log(orderId, employeeId, "EN_PREPARACION", "LISTO", startSecond + 2),
                log(orderId, employeeId, "LISTO", "ENTREGADO", startSecond + seconds));
    }

    @Test
    void showsTheTimeOfEachDeliveredOrderAndTheEmployeeRanking() {
        when(restaurantDirectoryPort.findOwnerId(RESTAURANT_ID)).thenReturn(Optional.of(OWNER_ID));
        List<OrderLog> logs = new ArrayList<>();
        logs.addAll(delivered(1, 9, 0, 600));       // empleado 9: 10 minutos
        logs.addAll(delivered(2, 9, 100, 1800));    // empleado 9: 30 minutos
        logs.addAll(delivered(3, 4, 200, 905));     // empleado 4: 15 minutos y 5 segundos
        logs.add(log(4, null, null, "PENDIENTE", 300));                // cancelado: no cuenta
        logs.add(log(4, null, "PENDIENTE", "CANCELADO", 320));
        logs.add(log(5, null, null, "PENDIENTE", 400));                // en preparación: no cuenta
        logs.add(log(5, 4L, "PENDIENTE", "EN_PREPARACION", 410));
        when(orderLogRepositoryPort.findByRestaurantId(RESTAURANT_ID)).thenReturn(logs);

        RestaurantEfficiencyResponse response = service.execute(RESTAURANT_ID, OWNER_ID);

        assertThat(response.getRestaurantId()).isEqualTo(RESTAURANT_ID);
        assertThat(response.getOrders()).extracting(OrderEfficiencyResponse::getOrderId).containsExactly(1L, 2L, 3L);
        assertThat(response.getOrders()).extracting(OrderEfficiencyResponse::getDurationSeconds)
                .containsExactly(600L, 1800L, 905L);
        assertThat(response.getOrders()).extracting(OrderEfficiencyResponse::getDuration)
                .containsExactly("00:10:00", "00:30:00", "00:15:05");
        assertThat(response.getOrders().get(0).getEmployeeId()).isEqualTo(9L);
        assertThat(response.getEmployeeRanking()).extracting(EmployeeRankingResponse::getEmployeeId)
                .containsExactly(4L, 9L);
        assertThat(response.getEmployeeRanking()).extracting(EmployeeRankingResponse::getPosition)
                .containsExactly(1, 2);
        assertThat(response.getEmployeeRanking()).extracting(EmployeeRankingResponse::getDeliveredOrders)
                .containsExactly(1, 2);
        assertThat(response.getEmployeeRanking()).extracting(EmployeeRankingResponse::getAverageDuration)
                .containsExactly("00:15:05", "00:20:00");
    }

    @Test
    void aRestaurantWithoutDeliveredOrdersHasEmptyLists() {
        when(restaurantDirectoryPort.findOwnerId(RESTAURANT_ID)).thenReturn(Optional.of(OWNER_ID));
        when(orderLogRepositoryPort.findByRestaurantId(RESTAURANT_ID)).thenReturn(List.of());

        RestaurantEfficiencyResponse response = service.execute(RESTAURANT_ID, OWNER_ID);

        assertThat(response.getOrders()).isEmpty();
        assertThat(response.getEmployeeRanking()).isEmpty();
    }

    @Test
    void anOwnerCannotSeeTheEfficiencyOfAnotherRestaurant() {
        when(restaurantDirectoryPort.findOwnerId(RESTAURANT_ID)).thenReturn(Optional.of(OWNER_ID));

        assertThatThrownBy(() -> service.execute(RESTAURANT_ID, 3L)).isInstanceOf(ForbiddenOperationException.class);
        verify(orderLogRepositoryPort, never()).findByRestaurantId(any());
    }

    @Test
    void anUnknownRestaurantIsNotFound() {
        when(restaurantDirectoryPort.findOwnerId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(99L, OWNER_ID)).isInstanceOf(RestaurantNotFoundException.class);
    }
}
