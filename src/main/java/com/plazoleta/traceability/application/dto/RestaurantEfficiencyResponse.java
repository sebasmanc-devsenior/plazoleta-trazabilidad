package com.plazoleta.traceability.application.dto;

import java.util.List;

/** HU-18: eficiencia de los pedidos de un restaurante. */
public class RestaurantEfficiencyResponse {
    private final Long restaurantId;
    private final List<OrderEfficiencyResponse> orders;
    private final List<EmployeeRankingResponse> employeeRanking;

    public RestaurantEfficiencyResponse(Long restaurantId, List<OrderEfficiencyResponse> orders,
                                        List<EmployeeRankingResponse> employeeRanking) {
        this.restaurantId = restaurantId;
        this.orders = List.copyOf(orders);
        this.employeeRanking = List.copyOf(employeeRanking);
    }

    public Long getRestaurantId() { return restaurantId; }
    public List<OrderEfficiencyResponse> getOrders() { return orders; }
    public List<EmployeeRankingResponse> getEmployeeRanking() { return employeeRanking; }
}
