package com.plazoleta.traceability.infrastructure.input.rest;

import com.plazoleta.traceability.domain.util.MensajesError;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.handler.IOrderLogHandler;
import com.plazoleta.traceability.infrastructure.input.rest.dto.RecordOrderLogRequest;
import com.plazoleta.traceability.infrastructure.input.rest.mapper.OrderLogRequestMapper;
import com.plazoleta.traceability.infrastructure.input.rest.exceptionhandler.UnauthorizedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Ruta entre microservicios: solo la llama restaurantes, con el token interno. */
@RestController
@RequestMapping("/internal/order-logs")
@Tag(name = "Order logs")
public class InternalOrderLogController {
    private final IOrderLogHandler orderLogHandler;
    private final String internalToken;

    public InternalOrderLogController(IOrderLogHandler orderLogHandler,
                                      @Value("${app.internal.token}") String internalToken) {
        this.orderLogHandler = orderLogHandler;
        this.internalToken = internalToken;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "HU-17 Record an order status change",
            description = "Internal route. Needs the header Authorization: Bearer <internal token>.")
    public OrderLogResponse record(@RequestBody RecordOrderLogRequest request,
                                   @RequestHeader(name = "Authorization", required = false) String authorization) {
        String supplied = authorization != null && authorization.startsWith("Bearer ")
                ? authorization.substring(7) : "";
        if (internalToken == null || internalToken.isBlank()
                || !MessageDigest.isEqual(internalToken.getBytes(StandardCharsets.UTF_8),
                        supplied.getBytes(StandardCharsets.UTF_8))) {
            throw new UnauthorizedException(MensajesError.CREDENCIALES_DE_SERVICIO_INVALIDAS);
        }
        return orderLogHandler.recordOrderLog(OrderLogRequestMapper.toCommand(request));
    }
}
