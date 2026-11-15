package com.plazoleta.traceability.application.port.input;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.dto.RecordOrderLogCommand;

/** HU-17: crear el registro de un cambio de estado de un pedido. */
public interface RecordOrderLogPort {
    OrderLogResponse execute(RecordOrderLogCommand command);
}
