package com.plazoleta.traceability.infrastructure.input.rest.mapper;

import com.plazoleta.traceability.application.dto.RecordOrderLogCommand;
import com.plazoleta.traceability.infrastructure.input.rest.dto.RecordOrderLogRequest;

/** Convierte el request HTTP en el comando de la capa de aplicación. */
public final class OrderLogRequestMapper {
    private OrderLogRequestMapper() {
    }

    public static RecordOrderLogCommand toCommand(RecordOrderLogRequest request) {
        return request == null ? null : new RecordOrderLogCommand(request.orderId(), request.clientId(),
                request.restaurantId(), request.employeeId(), request.previousStatus(), request.newStatus());
    }
}
