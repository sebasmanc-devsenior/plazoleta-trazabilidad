package com.plazoleta.traceability.infrastructure.output.http;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.Duration;
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

    /** Constructor para las pruebas: usa los tiempos de espera por defecto. */
    public RemoteRestaurantDirectoryAdapter(String baseUrl, String internalToken) {
        this(baseUrl, internalToken, HttpClients.DEFAULT_CONNECT_TIMEOUT, HttpClients.DEFAULT_READ_TIMEOUT);
    }

    @Autowired
    public RemoteRestaurantDirectoryAdapter(@Value("${app.restaurants.base-url}") String baseUrl, @Value("${app.internal.token}") String internalToken,
            @Value("${app.http.connect-timeout:2s}") Duration connectTimeout,
            @Value("${app.http.read-timeout:5s}") Duration readTimeout) {
        this.client = HttpClients.restClient(baseUrl, connectTimeout, readTimeout);
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
