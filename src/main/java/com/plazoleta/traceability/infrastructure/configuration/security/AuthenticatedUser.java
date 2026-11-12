package com.plazoleta.traceability.infrastructure.configuration.security;

/** Usuario que viene en el JWT emitido por el microservicio de usuarios. */
public record AuthenticatedUser(Long userId, String role) {
}
