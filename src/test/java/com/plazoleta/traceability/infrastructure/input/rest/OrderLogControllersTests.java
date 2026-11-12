package com.plazoleta.traceability.infrastructure.input.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.domain.model.OrderLog;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** HU-17: las dos rutas del microservicio. MongoDB se reemplaza por un doble: las pruebas no lo necesitan. */
@SpringBootTest
@AutoConfigureMockMvc
class OrderLogControllersTests {
    private static final String SECRET = "test-only-secret-key-with-at-least-32-bytes!!";
    private static final String BODY =
            "{\"orderId\":5,\"clientId\":7,\"restaurantId\":1,\"employeeId\":9,"
            + "\"previousStatus\":\"PENDIENTE\",\"newStatus\":\"EN_PREPARACION\"}";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private OrderLogRepositoryPort orderLogRepositoryPort;

    private static String token(long userId, String role, String secret) {
        return "Bearer " + Jwts.builder().subject(String.valueOf(userId)).claim("userId", userId).claim("role", role)
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).compact();
    }

    private void orderFiveBelongsToClientSeven() {
        when(orderLogRepositoryPort.findByOrderId(5L)).thenReturn(List.of(
                new OrderLog("a", 5L, 7L, 1L, null, null, "PENDIENTE", LocalDateTime.of(2026, 11, 12, 12, 0)),
                new OrderLog("b", 5L, 7L, 1L, 9L, "PENDIENTE", "EN_PREPARACION",
                        LocalDateTime.of(2026, 11, 12, 12, 5))));
    }

    @Test
    void recordsAStatusChangeWithTheInternalToken() throws Exception {
        when(orderLogRepositoryPort.save(any())).thenAnswer(call -> call.getArgument(0));

        mockMvc.perform(post("/internal/order-logs").header("Authorization", "Bearer test-internal-token")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(5))
                .andExpect(jsonPath("$.newStatus").value("EN_PREPARACION"))
                .andExpect(jsonPath("$.changedAt").isNotEmpty());
    }

    @Test
    void doesNotRecordWithoutTheInternalToken() throws Exception {
        mockMvc.perform(post("/internal/order-logs").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/internal/order-logs").header("Authorization", token(7, "CLIENTE", SECRET))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        verify(orderLogRepositoryPort, never()).save(any());
    }

    @Test
    void rejectsAnIncompleteStatusChange() throws Exception {
        mockMvc.perform(post("/internal/order-logs").header("Authorization", "Bearer test-internal-token")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"orderId\":5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void theClientSeesTheHistoryOfTheirOrder() throws Exception {
        orderFiveBelongsToClientSeven();

        mockMvc.perform(get("/api/orders/5/logs").header("Authorization", token(7, "CLIENTE", SECRET)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].newStatus").value("PENDIENTE"))
                .andExpect(jsonPath("$[1].previousStatus").value("PENDIENTE"))
                .andExpect(jsonPath("$[1].newStatus").value("EN_PREPARACION"))
                .andExpect(jsonPath("$[1].employeeId").value(9));
    }

    @Test
    void anotherClientCannotSeeThatHistory() throws Exception {
        orderFiveBelongsToClientSeven();

        mockMvc.perform(get("/api/orders/5/logs").header("Authorization", token(8, "CLIENTE", SECRET)))
                .andExpect(status().isForbidden());
    }

    @Test
    void onlyClientsCanAskForTheHistory() throws Exception {
        mockMvc.perform(get("/api/orders/5/logs").header("Authorization", token(9, "EMPLEADO", SECRET)))
                .andExpect(status().isForbidden());
    }

    @Test
    void theHistoryNeedsAValidToken() throws Exception {
        mockMvc.perform(get("/api/orders/5/logs")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/orders/5/logs").header("Authorization",
                        token(7, "CLIENTE", "another-secret-key-with-at-least-32-bytes!!")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anOrderWithoutLogsIsNotFound() throws Exception {
        when(orderLogRepositoryPort.findByOrderId(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/orders/99/logs").header("Authorization", token(7, "CLIENTE", SECRET)))
                .andExpect(status().isNotFound());
    }
}
