package com.plazoleta.traceability.domain.exception;

/** Otro microservicio no respondió (HTTP 503). */
public class IntegrationUnavailableException extends RuntimeException {
    public IntegrationUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
