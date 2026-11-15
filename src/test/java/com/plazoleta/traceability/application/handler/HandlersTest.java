package com.plazoleta.traceability.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.dto.RecordOrderLogCommand;
import com.plazoleta.traceability.application.dto.RestaurantEfficiencyResponse;
import com.plazoleta.traceability.application.port.input.GetOrderLogsPort;
import com.plazoleta.traceability.application.port.input.GetRestaurantEfficiencyPort;
import com.plazoleta.traceability.application.port.input.RecordOrderLogPort;

/** Los handlers no deciden nada: pasan los datos al caso de uso y devuelven su respuesta. */
class HandlersTest {
    private final RecordOrderLogPort recordOrderLogUseCase = mock(RecordOrderLogPort.class);
    private final GetOrderLogsPort getOrderLogsUseCase = mock(GetOrderLogsPort.class);
    private final GetRestaurantEfficiencyPort getRestaurantEfficiencyUseCase =
            mock(GetRestaurantEfficiencyPort.class);
    private final OrderLogHandler orderLogHandler = new OrderLogHandler(recordOrderLogUseCase, getOrderLogsUseCase);
    private final EfficiencyHandler efficiencyHandler = new EfficiencyHandler(getRestaurantEfficiencyUseCase);

    @Test
    void recordOrderLogDelegatesToTheUseCase() {
        RecordOrderLogCommand command = new RecordOrderLogCommand(1L, 2L, 3L, null, null, "PENDIENTE");
        OrderLogResponse expected = mock(OrderLogResponse.class);
        when(recordOrderLogUseCase.execute(command)).thenReturn(expected);

        assertThat(orderLogHandler.recordOrderLog(command)).isSameAs(expected);
        verify(recordOrderLogUseCase).execute(command);
    }

    @Test
    void getOrderLogsPassesTheOrderAndTheClientFromTheToken() {
        List<OrderLogResponse> expected = List.of(mock(OrderLogResponse.class));
        when(getOrderLogsUseCase.execute(7L, 2L)).thenReturn(expected);

        assertThat(orderLogHandler.getOrderLogs(7L, 2L)).isSameAs(expected);
        verify(getOrderLogsUseCase).execute(7L, 2L);
    }

    @Test
    void getRestaurantEfficiencyPassesTheRestaurantAndTheOwnerFromTheToken() {
        RestaurantEfficiencyResponse expected = mock(RestaurantEfficiencyResponse.class);
        when(getRestaurantEfficiencyUseCase.execute(3L, 9L)).thenReturn(expected);

        assertThat(efficiencyHandler.getRestaurantEfficiency(3L, 9L)).isSameAs(expected);
        verify(getRestaurantEfficiencyUseCase).execute(3L, 9L);
    }
}
