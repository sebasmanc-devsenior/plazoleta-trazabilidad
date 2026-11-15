package com.plazoleta.traceability.application.usecase;

import com.plazoleta.traceability.domain.util.MensajesError;

import java.util.List;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.port.input.GetOrderLogsPort;
import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.domain.exception.ForbiddenOperationException;
import com.plazoleta.traceability.domain.exception.OrderLogsNotFoundException;
import com.plazoleta.traceability.domain.model.OrderLog;

public class GetOrderLogsUseCase implements GetOrderLogsPort {
    private final OrderLogRepositoryPort orderLogRepositoryPort;

    public GetOrderLogsUseCase(OrderLogRepositoryPort orderLogRepositoryPort) {
        this.orderLogRepositoryPort = orderLogRepositoryPort;
    }

    @Override
    public List<OrderLogResponse> execute(Long orderId, Long clientId) {
        List<OrderLog> logs = orderLogRepositoryPort.findByOrderId(orderId);
        if (logs.isEmpty()) {
            throw new OrderLogsNotFoundException(MensajesError.NO_HAY_REGISTROS_PARA_ESTE_PEDIDO);
        }
        // Un cliente solo consulta el historial de sus propios pedidos.
        if (logs.stream().anyMatch(log -> !log.getClientId().equals(clientId))) {
            throw new ForbiddenOperationException(MensajesError.PEDIDO_PERTENECE_A_OTRO_CLIENTE);
        }
        return logs.stream().map(OrderLogResponse::from).toList();
    }
}
