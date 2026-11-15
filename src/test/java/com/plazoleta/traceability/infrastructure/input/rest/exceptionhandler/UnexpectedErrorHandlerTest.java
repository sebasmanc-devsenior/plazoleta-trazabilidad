package com.plazoleta.traceability.infrastructure.input.rest.exceptionhandler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.plazoleta.traceability.domain.util.MensajesError;

/** Un error no previsto responde 500 con el formato del proyecto, sin detalles internos. */
class UnexpectedErrorHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/algo");

    @Test
    void anUnexpectedErrorAnswers500WithoutInternalDetails() {
        var response = handler.handleUnexpected(new RuntimeException("NullPointer en la línea 42 con SQL interno"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo(MensajesError.ERROR_INTERNO_DEL_SERVIDOR);
        assertThat(response.getBody().message()).doesNotContain("SQL").doesNotContain("42");
    }

    @Test
    void aMethodThatIsNotAllowedKeepsItsOwnCode() {
        var response = handler.handleUnexpected(new HttpRequestMethodNotSupportedException("PATCH"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(405);
        assertThat(response.getBody().message()).isEqualTo(MensajesError.PETICION_NO_SOPORTADA);
    }
}
