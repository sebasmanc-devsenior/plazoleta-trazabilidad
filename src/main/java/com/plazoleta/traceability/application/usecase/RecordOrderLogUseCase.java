package com.plazoleta.traceability.application.usecase;

import com.plazoleta.traceability.domain.util.MensajesError;

import java.time.Clock;
import java.time.LocalDateTime;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.dto.RecordOrderLogCommand;
import com.plazoleta.traceability.application.port.input.RecordOrderLogPort;
import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.domain.model.OrderLog;

public class RecordOrderLogUseCase implements RecordOrderLogPort {
    private final OrderLogRepositoryPort orderLogRepositoryPort;
    private final Clock clock;

    public RecordOrderLogUseCase(OrderLogRepositoryPort orderLogRepositoryPort, Clock clock) {
        this.orderLogRepositoryPort = orderLogRepositoryPort;
        this.clock = clock;
    }

    @Override
    public OrderLogResponse execute(RecordOrderLogCommand command) {
        if (command == null) {
            throw new IllegalArgumentException(MensajesError.CUERPO_DE_LA_PETICION_ES_OBLIGATORIO);
        }
        // La fecha la pone este servicio al recibir el cambio: todos los registros usan el mismo reloj.
        OrderLog log = new OrderLog(null, command.getOrderId(), command.getClientId(), command.getRestaurantId(),
                command.getEmployeeId(), command.getPreviousStatus(), command.getNewStatus(),
                LocalDateTime.now(clock));
        return OrderLogResponse.from(orderLogRepositoryPort.save(log));
    }
}
