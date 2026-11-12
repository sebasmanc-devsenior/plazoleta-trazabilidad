package com.plazoleta.traceability.domain.exception;

/** No hay registros para ese pedido (HTTP 404). */
public class OrderLogsNotFoundException extends RuntimeException {
    public OrderLogsNotFoundException(String message) {
        super(message);
    }
}
