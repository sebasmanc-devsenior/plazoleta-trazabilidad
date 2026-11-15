package com.plazoleta.traceability.application.handler;

import com.plazoleta.traceability.application.dto.RestaurantEfficiencyResponse;

/** Handler de la eficiencia de pedidos (HU-18). Sin reglas de negocio: delega en el caso de uso. */
public interface IEfficiencyHandler {
    RestaurantEfficiencyResponse getRestaurantEfficiency(Long restaurantId, Long ownerId);
}
