package com.plazoleta.traceability.application.port.output;

import java.util.List;

import com.plazoleta.traceability.domain.model.OrderLog;

public interface OrderLogRepositoryPort {
    OrderLog save(OrderLog log);

    /** Registros de un pedido, del más antiguo al más reciente. */
    List<OrderLog> findByOrderId(Long orderId);
}
