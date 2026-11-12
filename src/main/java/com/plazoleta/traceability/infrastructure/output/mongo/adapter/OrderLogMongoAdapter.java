package com.plazoleta.traceability.infrastructure.output.mongo.adapter;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.domain.model.OrderLog;
import com.plazoleta.traceability.infrastructure.output.mongo.document.OrderLogDocument;
import com.plazoleta.traceability.infrastructure.output.mongo.repository.OrderLogMongoRepository;

@Repository
public class OrderLogMongoAdapter implements OrderLogRepositoryPort {
    private final OrderLogMongoRepository orderLogMongoRepository;

    public OrderLogMongoAdapter(OrderLogMongoRepository orderLogMongoRepository) {
        this.orderLogMongoRepository = orderLogMongoRepository;
    }

    @Override
    public OrderLog save(OrderLog log) {
        return orderLogMongoRepository.save(OrderLogDocument.fromDomain(log)).toDomain();
    }

    @Override
    public List<OrderLog> findByOrderId(Long orderId) {
        return orderLogMongoRepository.findByOrderIdOrderByChangedAtAsc(orderId).stream()
                .map(OrderLogDocument::toDomain).toList();
    }
}
