package com.plazoleta.traceability.infrastructure.input.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.traceability.application.dto.RestaurantEfficiencyResponse;
import com.plazoleta.traceability.application.port.input.GetRestaurantEfficiencyUseCase;
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
@Tag(name = "Efficiency")
public class EfficiencyController {
    private final GetRestaurantEfficiencyUseCase getRestaurantEfficiencyUseCase;
    private final JwtTokenReader jwtTokenReader;

    public EfficiencyController(GetRestaurantEfficiencyUseCase getRestaurantEfficiencyUseCase,
                                JwtTokenReader jwtTokenReader) {
        this.getRestaurantEfficiencyUseCase = getRestaurantEfficiencyUseCase;
        this.jwtTokenReader = jwtTokenReader;
    }

    @GetMapping("/restaurants/{restaurantId}/efficiency")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "HU-18 Order efficiency",
            description = "PROPIETARIO only, and only for their own restaurant. Time of each delivered order from "
                    + "PENDIENTE to ENTREGADO, and a ranking of employees by average time (fastest first). "
                    + "Cancelled orders and orders still in progress are not counted.")
    public RestaurantEfficiencyResponse getEfficiency(@PathVariable Long restaurantId,
            @Parameter(hidden = true) @RequestHeader(name = "Authorization", required = false) String authorization) {
        AuthenticatedUser user = jwtTokenReader.read(authorization)
                .orElseThrow(() -> new UnauthorizedException("Se requiere un token válido"));
        if (!"PROPIETARIO".equals(user.role())) {
            throw new ForbiddenOperationException("Solo el propietario puede ver la eficiencia de un restaurante");
        }
        return getRestaurantEfficiencyUseCase.execute(restaurantId, user.userId());
    }
}
