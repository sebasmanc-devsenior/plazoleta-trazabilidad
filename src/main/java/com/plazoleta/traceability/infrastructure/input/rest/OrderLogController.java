package com.plazoleta.traceability.infrastructure.input.rest;

import com.plazoleta.traceability.domain.util.MensajesError;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.traceability.application.dto.OrderLogResponse;
import com.plazoleta.traceability.application.port.input.GetOrderLogsUseCase;
import com.plazoleta.traceability.domain.exception.ForbiddenOperationException;
import com.plazoleta.traceability.infrastructure.configuration.security.AuthenticatedUser;
import com.plazoleta.traceability.infrastructure.configuration.security.JwtTokenReader;
import com.plazoleta.traceability.infrastructure.input.rest.exceptionhandler.UnauthorizedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Order logs")
public class OrderLogController {
    private final GetOrderLogsUseCase getOrderLogsUseCase;
    private final JwtTokenReader jwtTokenReader;

    public OrderLogController(GetOrderLogsUseCase getOrderLogsUseCase, JwtTokenReader jwtTokenReader) {
        this.getOrderLogsUseCase = getOrderLogsUseCase;
        this.jwtTokenReader = jwtTokenReader;
    }

    @GetMapping("/orders/{orderId}/logs")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "HU-17 Order status history",
            description = "CLIENTE only, and only for their own orders. Oldest change first.")
    public List<OrderLogResponse> getOrderLogs(@PathVariable Long orderId,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization", required = false) String authorization) {
        AuthenticatedUser user = jwtTokenReader.read(authorization)
                .orElseThrow(() -> new UnauthorizedException(MensajesError.REQUIERE_UN_TOKEN_VALIDO));
        if (!"CLIENTE".equals(user.role())) {
            throw new ForbiddenOperationException(MensajesError.SOLO_UN_CLIENTE_PUEDE_VER_EL_HISTORIAL);
        }
        return getOrderLogsUseCase.execute(orderId, user.userId());
    }
}
