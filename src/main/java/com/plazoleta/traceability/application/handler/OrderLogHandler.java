package com.plazoleta.traceability.application.handler;

import java.util.List;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.dto.RecordOrderLogCommand;
import com.plazoleta.traceability.application.port.input.GetOrderLogsUseCase;
import com.plazoleta.traceability.application.port.input.RecordOrderLogUseCase;

/** Delega en los casos de uso de la HU-17. Se registra como @Bean en BeanConfiguration (sin Spring aquí). */
public class OrderLogHandler implements IOrderLogHandler {
    private final RecordOrderLogUseCase recordOrderLogUseCase;
    private final GetOrderLogsUseCase getOrderLogsUseCase;

    public OrderLogHandler(RecordOrderLogUseCase recordOrderLogUseCase, GetOrderLogsUseCase getOrderLogsUseCase) {
        this.recordOrderLogUseCase = recordOrderLogUseCase;
        this.getOrderLogsUseCase = getOrderLogsUseCase;
    }

    @Override
    public OrderLogResponse recordOrderLog(RecordOrderLogCommand command) {
        return recordOrderLogUseCase.execute(command);
    }

    @Override
    public List<OrderLogResponse> getOrderLogs(Long orderId, Long clientId) {
        return getOrderLogsUseCase.execute(orderId, clientId);
    }
}
