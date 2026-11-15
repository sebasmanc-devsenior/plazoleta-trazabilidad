package com.plazoleta.traceability.infrastructure.input.rest.exceptionhandler;

import com.plazoleta.traceability.domain.util.MensajesError;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.plazoleta.traceability.domain.exception.ForbiddenOperationException;
import com.plazoleta.traceability.domain.exception.IntegrationUnavailableException;
import com.plazoleta.traceability.domain.exception.OrderLogsNotFoundException;
import com.plazoleta.traceability.domain.exception.RestaurantNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleMalformed(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, MensajesError.PETICION_ESTA_MAL_FORMADA, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenOperationException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler({OrderLogsNotFoundException.class, RestaurantNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(IntegrationUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleIntegration(IntegrationUnavailableException ex,
                                                           HttpServletRequest request) {
        log.warn("Integration unavailable at {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabase(DataAccessException ex, HttpServletRequest request) {
        log.warn("MongoDB is unavailable at {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.SERVICE_UNAVAILABLE, MensajesError.BASE_DE_DATOS_DE_TRAZABILIDAD_NO_ESTA, request);
    }

    /** Error no previsto: 500 con el formato del proyecto y sin detalles internos (el detalle queda en el log). */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        if (ex instanceof org.springframework.web.ErrorResponse frameworkError) {
            HttpStatus status = HttpStatus.valueOf(frameworkError.getStatusCode().value());
            return build(status, status == HttpStatus.NOT_FOUND ? MensajesError.RUTA_NO_EXISTE
                    : MensajesError.PETICION_NO_SOPORTADA, request);
        }
        log.error("Unexpected error at {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, MensajesError.ERROR_INTERNO_DEL_SERVIDOR, request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponse(Instant.now(), status.value(),
                status.getReasonPhrase(), message, request.getRequestURI()));
    }

    public record ErrorResponse(Instant timestamp, int status, String error, String message, String path) {
    }
}
