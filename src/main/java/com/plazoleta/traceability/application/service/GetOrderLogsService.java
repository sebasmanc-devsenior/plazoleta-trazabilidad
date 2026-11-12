package com.plazoleta.traceability.application.service;

import java.util.List;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.port.input.GetOrderLogsUseCase;
import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.domain.exception.ForbiddenOperationException;
import com.plazoleta.traceability.domain.exception.OrderLogsNotFoundException;
import com.plazoleta.traceability.domain.model.OrderLog;

public class GetOrderLogsService implements GetOrderLogsUseCase {
    private final OrderLogRepositoryPort orderLogRepositoryPort;

    public GetOrderLogsService(OrderLogRepositoryPort orderLogRepositoryPort) {
        this.orderLogRepositoryPort = orderLogRepositoryPort;
    }

    @Override
    public List<OrderLogResponse> execute(Long orderId, Long clientId) {
        List<OrderLog> logs = orderLogRepositoryPort.findByOrderId(orderId);
        if (logs.isEmpty()) {
            throw new OrderLogsNotFoundException("There are no logs for this order");
        }
        // Un cliente solo consulta el historial de sus propios pedidos.
        if (logs.stream().anyMatch(log -> !log.getClientId().equals(clientId))) {
            throw new ForbiddenOperationException("The order belongs to another client");
        }
        return logs.stream().map(OrderLogResponse::from).toList();
    }
}
