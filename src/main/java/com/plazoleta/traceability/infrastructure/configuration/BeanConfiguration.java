package com.plazoleta.traceability.infrastructure.configuration;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.plazoleta.traceability.application.handler.EfficiencyHandler;
import com.plazoleta.traceability.application.handler.IEfficiencyHandler;
import com.plazoleta.traceability.application.handler.IOrderLogHandler;
import com.plazoleta.traceability.application.handler.OrderLogHandler;
import com.plazoleta.traceability.application.port.input.GetOrderLogsPort;
import com.plazoleta.traceability.application.port.input.GetRestaurantEfficiencyPort;
import com.plazoleta.traceability.application.port.input.RecordOrderLogPort;
import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.application.port.output.RestaurantDirectoryPort;
import com.plazoleta.traceability.application.usecase.GetOrderLogsUseCase;
import com.plazoleta.traceability.application.usecase.GetRestaurantEfficiencyUseCase;
import com.plazoleta.traceability.application.usecase.RecordOrderLogUseCase;

/** Conecta los casos de uso con sus adaptadores. */
@Configuration
public class BeanConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    /** HU-17 */
    @Bean
    public RecordOrderLogPort recordOrderLogUseCase(OrderLogRepositoryPort logs, Clock clock) {
        return new RecordOrderLogUseCase(logs, clock);
    }

    /** HU-17 */
    @Bean
    public GetOrderLogsPort getOrderLogsUseCase(OrderLogRepositoryPort logs) {
        return new GetOrderLogsUseCase(logs);
    }

    /** HU-18 */
    @Bean
    public GetRestaurantEfficiencyPort getRestaurantEfficiencyUseCase(OrderLogRepositoryPort logs,
                                                                         RestaurantDirectoryPort restaurants) {
        return new GetRestaurantEfficiencyUseCase(logs, restaurants);
    }

    /** Handler de la HU-17 que usan los controladores del historial. */
    @Bean
    public IOrderLogHandler orderLogHandler(RecordOrderLogPort recordOrderLogUseCase,
                                            GetOrderLogsPort getOrderLogsUseCase) {
        return new OrderLogHandler(recordOrderLogUseCase, getOrderLogsUseCase);
    }

    /** Handler de la HU-18 que usa el controlador de eficiencia. */
    @Bean
    public IEfficiencyHandler efficiencyHandler(GetRestaurantEfficiencyPort getRestaurantEfficiencyUseCase) {
        return new EfficiencyHandler(getRestaurantEfficiencyUseCase);
    }
}
