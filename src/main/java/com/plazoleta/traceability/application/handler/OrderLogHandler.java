package com.plazoleta.traceability.application.handler;

import java.util.List;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.dto.RecordOrderLogCommand;
import com.plazoleta.traceability.application.port.input.GetOrderLogsPort;
import com.plazoleta.traceability.application.port.input.RecordOrderLogPort;

/** Delega en los casos de uso de la HU-17. Se registra como @Bean en BeanConfiguration (sin Spring aquí). */
public class OrderLogHandler implements IOrderLogHandler {
    private final RecordOrderLogPort recordOrderLogUseCase;
    private final GetOrderLogsPort getOrderLogsUseCase;

    public OrderLogHandler(RecordOrderLogPort recordOrderLogUseCase, GetOrderLogsPort getOrderLogsUseCase) {
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
