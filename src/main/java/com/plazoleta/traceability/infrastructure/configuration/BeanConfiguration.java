package com.plazoleta.traceability.infrastructure.configuration;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.plazoleta.traceability.application.handler.EfficiencyHandler;
import com.plazoleta.traceability.application.handler.IEfficiencyHandler;
import com.plazoleta.traceability.application.handler.IOrderLogHandler;
import com.plazoleta.traceability.application.handler.OrderLogHandler;
import com.plazoleta.traceability.application.port.input.GetOrderLogsUseCase;
import com.plazoleta.traceability.application.port.input.GetRestaurantEfficiencyUseCase;
import com.plazoleta.traceability.application.port.input.RecordOrderLogUseCase;
import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.application.port.output.RestaurantDirectoryPort;
import com.plazoleta.traceability.application.service.GetOrderLogsService;
import com.plazoleta.traceability.application.service.GetRestaurantEfficiencyService;
import com.plazoleta.traceability.application.service.RecordOrderLogService;

/** Conecta los casos de uso con sus adaptadores. */
@Configuration
public class BeanConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    /** HU-17 */
    @Bean
    public RecordOrderLogUseCase recordOrderLogUseCase(OrderLogRepositoryPort logs, Clock clock) {
        return new RecordOrderLogService(logs, clock);
    }

    /** HU-17 */
    @Bean
    public GetOrderLogsUseCase getOrderLogsUseCase(OrderLogRepositoryPort logs) {
        return new GetOrderLogsService(logs);
    }

    /** HU-18 */
    @Bean
    public GetRestaurantEfficiencyUseCase getRestaurantEfficiencyUseCase(OrderLogRepositoryPort logs,
                                                                         RestaurantDirectoryPort restaurants) {
        return new GetRestaurantEfficiencyService(logs, restaurants);
    }

    /** Handler de la HU-17 que usan los controladores del historial. */
    @Bean
    public IOrderLogHandler orderLogHandler(RecordOrderLogUseCase recordOrderLogUseCase,
                                            GetOrderLogsUseCase getOrderLogsUseCase) {
        return new OrderLogHandler(recordOrderLogUseCase, getOrderLogsUseCase);
    }

    /** Handler de la HU-18 que usa el controlador de eficiencia. */
    @Bean
    public IEfficiencyHandler efficiencyHandler(GetRestaurantEfficiencyUseCase getRestaurantEfficiencyUseCase) {
        return new EfficiencyHandler(getRestaurantEfficiencyUseCase);
    }
}
