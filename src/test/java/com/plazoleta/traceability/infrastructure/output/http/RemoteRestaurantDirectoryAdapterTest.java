package com.plazoleta.traceability.infrastructure.output.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.plazoleta.traceability.domain.exception.IntegrationUnavailableException;
import com.sun.net.httpserver.HttpServer;

/** HU-18: el adaptador llama de verdad al microservicio de restaurantes (aquí, un servidor HTTP de prueba). */
class RemoteRestaurantDirectoryAdapterTest {
    private HttpServer restaurantsService;
    private RemoteRestaurantDirectoryAdapter adapter;
    private final AtomicReference<String> path = new AtomicReference<>();
    private final AtomicReference<String> authorization = new AtomicReference<>();

    @BeforeEach
    void startRestaurantsService() throws IOException {
        restaurantsService = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        restaurantsService.start();
        adapter = new RemoteRestaurantDirectoryAdapter(
                "http://127.0.0.1:" + restaurantsService.getAddress().getPort(), "test-internal-token");
    }

    @AfterEach
    void stopRestaurantsService() {
        restaurantsService.stop(0);
    }

    private void restaurantsServiceAnswers(int status, String json) {
        restaurantsService.createContext("/", exchange -> {
            path.set(exchange.getRequestURI().getPath());
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            byte[] body = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
    }

    @Test
    void readsTheOwnerOfTheRestaurantWithTheInternalToken() {
        restaurantsServiceAnswers(200, "{\"id\":1,\"name\":\"Sabor Paisa\",\"ownerUserId\":2}");

        assertThat(adapter.findOwnerId(1L)).contains(2L);
        assertThat(path.get()).isEqualTo("/internal/restaurants/1");
        assertThat(authorization.get()).isEqualTo("Bearer test-internal-token");
    }

    @Test
    void anUnknownRestaurantGivesEmpty() {
        restaurantsServiceAnswers(404, "{\"message\":\"Restaurant was not found\"}");

        assertThat(adapter.findOwnerId(99L)).isEmpty();
    }

    @Test
    void reportsTheServiceAsUnavailableWhenItDoesNotAnswer() {
        restaurantsService.stop(0);

        assertThatThrownBy(() -> adapter.findOwnerId(1L)).isInstanceOf(IntegrationUnavailableException.class);
    }
}
