package com.plazoleta.traceability.infrastructure.output.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.plazoleta.traceability.infrastructure.output.mongo.document.OrderLogDocument;

public interface OrderLogMongoRepository extends MongoRepository<OrderLogDocument, String> {
    List<OrderLogDocument> findByOrderIdOrderByChangedAtAsc(Long orderId);

    /** HU-18 */
    List<OrderLogDocument> findByRestaurantIdOrderByChangedAtAsc(Long restaurantId);
}
