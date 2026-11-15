package com.plazoleta.traceability.application.handler;

import com.plazoleta.traceability.application.dto.RestaurantEfficiencyResponse;
import com.plazoleta.traceability.application.port.input.GetRestaurantEfficiencyPort;

/** Delega en el caso de uso de la HU-18. Se registra como @Bean en BeanConfiguration (sin Spring aquí). */
public class EfficiencyHandler implements IEfficiencyHandler {
    private final GetRestaurantEfficiencyPort getRestaurantEfficiencyUseCase;

    public EfficiencyHandler(GetRestaurantEfficiencyPort getRestaurantEfficiencyUseCase) {
        this.getRestaurantEfficiencyUseCase = getRestaurantEfficiencyUseCase;
    }

    @Override
    public RestaurantEfficiencyResponse getRestaurantEfficiency(Long restaurantId, Long ownerId) {
        return getRestaurantEfficiencyUseCase.execute(restaurantId, ownerId);
    }
}
