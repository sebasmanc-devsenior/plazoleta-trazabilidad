package com.plazoleta.traceability.infrastructure.input.rest.dto;

/**
 * Cuerpo de POST /internal/order-logs (lo envía restaurantes). Es una ruta interna: los datos los valida el
 * caso de uso después de comprobar el token interno, para que un llamador sin token reciba 401 y no 400.
 */
public record RecordOrderLogRequest(Long orderId, Long clientId, Long restaurantId, Long employeeId,
                                    String previousStatus, String newStatus) {
}
