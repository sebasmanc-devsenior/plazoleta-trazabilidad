package com.plazoleta.traceability.domain.exception;

/** El usuario autenticado no tiene permiso sobre ese pedido (HTTP 403). */
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
