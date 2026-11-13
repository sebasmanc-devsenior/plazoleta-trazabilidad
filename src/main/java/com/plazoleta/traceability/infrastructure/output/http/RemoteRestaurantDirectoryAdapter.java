package com.plazoleta.traceability.infrastructure.output.http;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.plazoleta.traceability.application.port.output.RestaurantDirectoryPort;
import com.plazoleta.traceability.domain.exception.IntegrationUnavailableException;

/** HU-18: llamada HTTP a restaurantes: GET /internal/restaurants/{id} con el token entre servicios. */
@Component
public class RemoteRestaurantDirectoryAdapter implements RestaurantDirectoryPort {
    private final RestClient client;
    private final String internalToken;

    public RemoteRestaurantDirectoryAdapter(@Value("${app.restaurants.base-url}") String baseUrl,
                                            @Value("${app.internal.token}") String internalToken) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
        this.internalToken = internalToken;
    }

    @Override
    public Optional<Long> findOwnerId(Long restaurantId) {
        try {
            RestaurantResponse restaurant = client.get().uri("/internal/restaurants/{id}", restaurantId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + internalToken)
                    .retrieve().body(RestaurantResponse.class);
            return Optional.ofNullable(restaurant).map(RestaurantResponse::ownerUserId);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return Optional.empty();
            }
            throw new IntegrationUnavailableException("Restaurants service rejected the lookup (HTTP "
                    + ex.getStatusCode().value() + ")", ex);
        } catch (RestClientException ex) {
            throw new IntegrationUnavailableException("Restaurants service is unavailable", ex);
        }
    }

    /** Parte de la respuesta de restaurantes que se necesita aquí. */
    private record RestaurantResponse(Long id, Long ownerUserId) {
    }
}
