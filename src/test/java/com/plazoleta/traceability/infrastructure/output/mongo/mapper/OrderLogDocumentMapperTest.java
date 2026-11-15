package com.plazoleta.traceability.infrastructure.output.mongo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.plazoleta.traceability.domain.model.OrderLog;
import com.plazoleta.traceability.infrastructure.output.mongo.document.OrderLogDocument;

/** El mapper pasa el registro del dominio al documento de MongoDB y de vuelta sin perder ningún dato. */
class OrderLogDocumentMapperTest {

    @Test
    void aStatusChangeGoesToTheDocumentAndBackWithAllItsFields() {
        OrderLog log = new OrderLog("abc123", 20L, 5L, 3L, 8L, "EN_PREPARACION", "LISTO",
                LocalDateTime.of(2026, 11, 15, 12, 30, 5));

        OrderLogDocument document = OrderLogDocumentMapper.toDocument(log);

        assertThat(document.getNewStatus()).isEqualTo("LISTO");
        assertThat(OrderLogDocumentMapper.toDomain(document)).usingRecursiveComparison().isEqualTo(log);
    }

    @Test
    void theFirstRecordOfAnOrderHasNoPreviousStatusNorEmployee() {
        OrderLog created = new OrderLog(null, 21L, 5L, 3L, null, null, "PENDIENTE",
                LocalDateTime.of(2026, 11, 15, 12, 0));

        OrderLog back = OrderLogDocumentMapper.toDomain(OrderLogDocumentMapper.toDocument(created));

        assertThat(back).usingRecursiveComparison().isEqualTo(created);
        assertThat(back.getPreviousStatus()).isNull();
        assertThat(back.getEmployeeId()).isNull();
    }
}
