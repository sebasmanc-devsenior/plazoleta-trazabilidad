package com.plazoleta.traceability.application.handler;

import java.util.List;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.dto.RecordOrderLogCommand;

/**
 * Handler del historial de pedidos (HU-17): lo que los controladores le piden a la capa de aplicación.
 * No tiene reglas de negocio, solo delega en los casos de uso por sus interfaces (puertos de entrada).
 */
public interface IOrderLogHandler {
    OrderLogResponse recordOrderLog(RecordOrderLogCommand command);

    List<OrderLogResponse> getOrderLogs(Long orderId, Long clientId);
}
