package com.plazoleta.traceability.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.dto.RecordOrderLogCommand;
import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.domain.model.OrderLog;

/** HU-17, criterio 1: se crea un registro por cada cambio de estado de un pedido. */
@ExtendWith(MockitoExtension.class)
class RecordOrderLogUseCaseTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-12T17:00:00Z"), ZoneId.of("America/Bogota"));

    @Mock
    private OrderLogRepositoryPort orderLogRepositoryPort;

    private RecordOrderLogUseCase service;

    @BeforeEach
    void setUp() {
        service = new RecordOrderLogUseCase(orderLogRepositoryPort, CLOCK);
    }

    @Test
    void savesTheStatusChangeWithTheDateOfThisService() {
        when(orderLogRepositoryPort.save(any())).thenAnswer(call -> call.getArgument(0));

        OrderLogResponse response = service.execute(
                new RecordOrderLogCommand(5L, 7L, 1L, 9L, "PENDIENTE", "EN_PREPARACION"));

        ArgumentCaptor<OrderLog> saved = ArgumentCaptor.forClass(OrderLog.class);
        verify(orderLogRepositoryPort).save(saved.capture());
        assertThat(saved.getValue().getOrderId()).isEqualTo(5L);
        assertThat(saved.getValue().getClientId()).isEqualTo(7L);
        assertThat(saved.getValue().getRestaurantId()).isEqualTo(1L);
        assertThat(saved.getValue().getEmployeeId()).isEqualTo(9L);
        assertThat(saved.getValue().getPreviousStatus()).isEqualTo("PENDIENTE");
        assertThat(saved.getValue().getNewStatus()).isEqualTo("EN_PREPARACION");
        assertThat(saved.getValue().getChangedAt()).isEqualTo(LocalDateTime.of(2026, 11, 12, 12, 0));
        assertThat(response.getNewStatus()).isEqualTo("EN_PREPARACION");
        assertThat(response.getChangedAt()).isEqualTo(LocalDateTime.of(2026, 11, 12, 12, 0));
    }

    @Test
    void theFirstLogOfAnOrderHasNoPreviousStatusNorEmployee() {
        when(orderLogRepositoryPort.save(any())).thenAnswer(call -> call.getArgument(0));

        OrderLogResponse response = service.execute(new RecordOrderLogCommand(5L, 7L, 1L, null, null, "PENDIENTE"));

        assertThat(response.getPreviousStatus()).isNull();
        assertThat(response.getEmployeeId()).isNull();
        assertThat(response.getNewStatus()).isEqualTo("PENDIENTE");
    }

    @Test
    void requiresOrderClientRestaurantAndNewStatus() {
        assertThatThrownBy(() -> service.execute(new RecordOrderLogCommand(null, 7L, 1L, null, null, "PENDIENTE")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.execute(new RecordOrderLogCommand(5L, null, 1L, null, null, "PENDIENTE")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.execute(new RecordOrderLogCommand(5L, 7L, null, null, null, "PENDIENTE")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.execute(new RecordOrderLogCommand(5L, 7L, 1L, null, "PENDIENTE", " ")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.execute(null)).isInstanceOf(IllegalArgumentException.class);
        verify(orderLogRepositoryPort, never()).save(any());
    }
}
