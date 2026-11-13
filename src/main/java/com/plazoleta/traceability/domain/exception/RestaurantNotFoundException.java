package com.plazoleta.traceability.domain.exception;

/** El restaurante no existe en el microservicio de restaurantes (HTTP 404). */
public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(String message) {
        super(message);
    }
}
