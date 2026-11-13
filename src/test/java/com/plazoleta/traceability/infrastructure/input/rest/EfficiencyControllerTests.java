package com.plazoleta.traceability.infrastructure.input.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.application.port.output.RestaurantDirectoryPort;
import com.plazoleta.traceability.domain.exception.IntegrationUnavailableException;
import com.plazoleta.traceability.domain.model.OrderLog;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** HU-18: la ruta de eficiencia. MongoDB y restaurantes se reemplazan por dobles de prueba. */
@SpringBootTest
@AutoConfigureMockMvc
class EfficiencyControllerTests {
    private static final String SECRET = "test-only-secret-key-with-at-least-32-bytes!!";
    private static final LocalDateTime T0 = LocalDateTime.of(2026, 11, 13, 12, 0);

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private OrderLogRepositoryPort orderLogRepositoryPort;
    @MockitoBean
    private RestaurantDirectoryPort restaurantDirectoryPort;

    private static String token(long userId, String role) {
        return "Bearer " + Jwts.builder().subject(String.valueOf(userId)).claim("userId", userId).claim("role", role)
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8))).compact();
    }

    @Test
    void theOwnerSeesTheEfficiencyOfTheirRestaurant() throws Exception {
        when(restaurantDirectoryPort.findOwnerId(1L)).thenReturn(Optional.of(2L));
        when(orderLogRepositoryPort.findByRestaurantId(1L)).thenReturn(List.of(
                new OrderLog("a", 5L, 7L, 1L, null, null, "PENDIENTE", T0),
                new OrderLog("b", 5L, 7L, 1L, 9L, "PENDIENTE", "EN_PREPARACION", T0.plusMinutes(1)),
                new OrderLog("c", 5L, 7L, 1L, 9L, "EN_PREPARACION", "LISTO", T0.plusMinutes(10)),
                new OrderLog("d", 5L, 7L, 1L, 9L, "LISTO", "ENTREGADO", T0.plusMinutes(12))));

        mockMvc.perform(get("/api/restaurants/1/efficiency").header("Authorization", token(2, "PROPIETARIO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.orders[0].orderId").value(5))
                .andExpect(jsonPath("$.orders[0].durationSeconds").value(720))
                .andExpect(jsonPath("$.orders[0].duration").value("00:12:00"))
                .andExpect(jsonPath("$.employeeRanking[0].position").value(1))
                .andExpect(jsonPath("$.employeeRanking[0].employeeId").value(9))
                .andExpect(jsonPath("$.employeeRanking[0].averageDuration").value("00:12:00"));
    }

    @Test
    void anotherOwnerIsForbidden() throws Exception {
        when(restaurantDirectoryPort.findOwnerId(1L)).thenReturn(Optional.of(2L));

        mockMvc.perform(get("/api/restaurants/1/efficiency").header("Authorization", token(3, "PROPIETARIO")))
                .andExpect(status().isForbidden());
    }

    @Test
    void onlyOwnersCanAsk() throws Exception {
        mockMvc.perform(get("/api/restaurants/1/efficiency").header("Authorization", token(2, "EMPLEADO")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/restaurants/1/efficiency").header("Authorization", token(2, "CLIENTE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void needsAToken() throws Exception {
        mockMvc.perform(get("/api/restaurants/1/efficiency")).andExpect(status().isUnauthorized());
    }

    @Test
    void anUnknownRestaurantIsNotFound() throws Exception {
        when(restaurantDirectoryPort.findOwnerId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/restaurants/99/efficiency").header("Authorization", token(2, "PROPIETARIO")))
                .andExpect(status().isNotFound());
    }

    @Test
    void answers503WhenTheRestaurantsServiceIsDown() throws Exception {
        when(restaurantDirectoryPort.findOwnerId(1L))
                .thenThrow(new IntegrationUnavailableException("Restaurants service is unavailable", null));

        mockMvc.perform(get("/api/restaurants/1/efficiency").header("Authorization", token(2, "PROPIETARIO")))
                .andExpect(status().isServiceUnavailable());
    }
}
