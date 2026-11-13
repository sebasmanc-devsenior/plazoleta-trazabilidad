package com.plazoleta.traceability.application.port.input;

import com.plazoleta.traceability.application.dto.RestaurantEfficiencyResponse;

/** HU-18: el propietario consulta la eficiencia de los pedidos de su restaurante. */
public interface GetRestaurantEfficiencyUseCase {
    RestaurantEfficiencyResponse execute(Long restaurantId, Long ownerId);
}
