package com.plazoleta.traceability.infrastructure.output.mongo.mapper;

import com.plazoleta.traceability.domain.model.OrderLog;
import com.plazoleta.traceability.infrastructure.output.mongo.document.OrderLogDocument;

/** Convierte entre el modelo del dominio OrderLog y el documento de MongoDB de la colección order_logs. */
public final class OrderLogDocumentMapper {
    private OrderLogDocumentMapper() {
    }

    public static OrderLogDocument toDocument(OrderLog log) {
        OrderLogDocument document = new OrderLogDocument();
        document.setId(log.getId());
        document.setOrderId(log.getOrderId());
        document.setClientId(log.getClientId());
        document.setRestaurantId(log.getRestaurantId());
        document.setEmployeeId(log.getEmployeeId());
        document.setPreviousStatus(log.getPreviousStatus());
        document.setNewStatus(log.getNewStatus());
        document.setChangedAt(log.getChangedAt());
        return document;
    }

    public static OrderLog toDomain(OrderLogDocument document) {
        return new OrderLog(document.getId(), document.getOrderId(), document.getClientId(),
                document.getRestaurantId(), document.getEmployeeId(), document.getPreviousStatus(),
                document.getNewStatus(), document.getChangedAt());
    }
}
