package com.plazoleta.traceability.application.port.input;

import java.util.List;

import com.plazoleta.traceability.application.dto.OrderLogResponse;

/** HU-17: el cliente consulta el historial de cambios de estado de uno de sus pedidos. */
public interface GetOrderLogsUseCase {
    List<OrderLogResponse> execute(Long orderId, Long clientId);
}
