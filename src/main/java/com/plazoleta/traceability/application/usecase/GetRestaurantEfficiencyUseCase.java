package com.plazoleta.traceability.application.usecase;

import com.plazoleta.traceability.domain.util.MensajesError;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.plazoleta.traceability.application.dto.EmployeeRankingResponse;
import com.plazoleta.traceability.application.dto.OrderEfficiencyResponse;
import com.plazoleta.traceability.application.dto.RestaurantEfficiencyResponse;
import com.plazoleta.traceability.application.port.input.GetRestaurantEfficiencyPort;
import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.application.port.output.RestaurantDirectoryPort;
import com.plazoleta.traceability.domain.exception.ForbiddenOperationException;
import com.plazoleta.traceability.domain.exception.RestaurantNotFoundException;
import com.plazoleta.traceability.domain.model.EmployeeEfficiency;
import com.plazoleta.traceability.domain.model.OrderEfficiency;
import com.plazoleta.traceability.domain.model.OrderLog;

public class GetRestaurantEfficiencyUseCase implements GetRestaurantEfficiencyPort {
    private final OrderLogRepositoryPort orderLogRepositoryPort;
    private final RestaurantDirectoryPort restaurantDirectoryPort;

    public GetRestaurantEfficiencyUseCase(OrderLogRepositoryPort orderLogRepositoryPort,
                                          RestaurantDirectoryPort restaurantDirectoryPort) {
        this.orderLogRepositoryPort = orderLogRepositoryPort;
        this.restaurantDirectoryPort = restaurantDirectoryPort;
    }

    @Override
    public RestaurantEfficiencyResponse execute(Long restaurantId, Long ownerId) {
        // Conexión con el microservicio de restaurantes: solo el dueño ve la eficiencia de su restaurante.
        Long realOwnerId = restaurantDirectoryPort.findOwnerId(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(MensajesError.RESTAURANTE_NO_EXISTE));
        if (!realOwnerId.equals(ownerId)) {
            throw new ForbiddenOperationException(MensajesError.RESTAURANTE_PERTENECE_A_OTRO_PROPIETARIO);
        }

        // Criterio 1: tiempo de cada pedido entregado, desde PENDIENTE hasta ENTREGADO.
        Map<Long, List<OrderLog>> logsByOrder = orderLogRepositoryPort.findByRestaurantId(restaurantId).stream()
                .collect(Collectors.groupingBy(OrderLog::getOrderId));
        List<OrderEfficiency> orders = logsByOrder.values().stream()
                .map(OrderEfficiency::fromLogs)
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(OrderEfficiency::getOrderId))
                .toList();

        // Criterio 2: ranking de empleados por tiempo medio.
        List<EmployeeEfficiency> ranking = EmployeeEfficiency.rank(orders);
        List<EmployeeRankingResponse> rankingResponse = new ArrayList<>();
        for (int i = 0; i < ranking.size(); i++) {
            rankingResponse.add(EmployeeRankingResponse.from(i + 1, ranking.get(i)));
        }
        return new RestaurantEfficiencyResponse(restaurantId,
                orders.stream().map(OrderEfficiencyResponse::from).toList(), rankingResponse);
    }
}
