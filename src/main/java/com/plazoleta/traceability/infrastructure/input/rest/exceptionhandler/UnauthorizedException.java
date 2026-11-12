package com.plazoleta.traceability.infrastructure.input.rest.exceptionhandler;

/** Falta el token, o no es válido (HTTP 401). */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
