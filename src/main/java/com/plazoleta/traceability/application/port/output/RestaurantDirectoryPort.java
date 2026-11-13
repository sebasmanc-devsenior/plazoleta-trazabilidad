package com.plazoleta.traceability.application.port.output;

import java.util.Optional;

/** HU-18: consulta del dueño de un restaurante. Lo resuelve el microservicio de restaurantes. */
public interface RestaurantDirectoryPort {
    /** Id del propietario del restaurante; vacío si el restaurante no existe. */
    Optional<Long> findOwnerId(Long restaurantId);
}
