package com.plazoleta.traceability.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.domain.exception.ForbiddenOperationException;
import com.plazoleta.traceability.domain.exception.OrderLogsNotFoundException;
import com.plazoleta.traceability.domain.model.OrderLog;

/** HU-17, criterio 2: cada cliente consulta el historial solo de sus propios pedidos. */
@ExtendWith(MockitoExtension.class)
class GetOrderLogsServiceTest {
    @Mock
    private OrderLogRepositoryPort orderLogRepositoryPort;
    @InjectMocks
    private GetOrderLogsService service;

    private static OrderLog log(String previous, String next, int minute, Long employeeId) {
        return new OrderLog("id" + minute, 5L, 7L, 1L, employeeId, previous, next,
                LocalDateTime.of(2026, 11, 12, 12, minute));
    }

    @Test
    void returnsTheHistoryOfTheClientOrderInOrder() {
        when(orderLogRepositoryPort.findByOrderId(5L)).thenReturn(List.of(
                log(null, "PENDIENTE", 0, null), log("PENDIENTE", "EN_PREPARACION", 5, 9L),
                log("EN_PREPARACION", "LISTO", 20, 9L)));

        List<OrderLogResponse> history = service.execute(5L, 7L);

        assertThat(history).extracting(OrderLogResponse::getNewStatus)
                .containsExactly("PENDIENTE", "EN_PREPARACION", "LISTO");
        assertThat(history.get(1).getPreviousStatus()).isEqualTo("PENDIENTE");
        assertThat(history.get(1).getEmployeeId()).isEqualTo(9L);
        assertThat(history.get(2).getChangedAt()).isEqualTo(LocalDateTime.of(2026, 11, 12, 12, 20));
    }

    @Test
    void aClientCannotSeeTheHistoryOfAnotherClientOrder() {
        when(orderLogRepositoryPort.findByOrderId(5L)).thenReturn(List.of(log(null, "PENDIENTE", 0, null)));

        assertThatThrownBy(() -> service.execute(5L, 8L)).isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void anOrderWithoutLogsIsNotFound() {
        when(orderLogRepositoryPort.findByOrderId(99L)).thenReturn(List.of());

        assertThatThrownBy(() -> service.execute(99L, 7L)).isInstanceOf(OrderLogsNotFoundException.class);
    }
}
