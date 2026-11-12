package com.plazoleta.traceability.infrastructure.configuration;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.plazoleta.traceability.application.port.input.GetOrderLogsUseCase;
import com.plazoleta.traceability.application.port.input.RecordOrderLogUseCase;
import com.plazoleta.traceability.application.port.output.OrderLogRepositoryPort;
import com.plazoleta.traceability.application.service.GetOrderLogsService;
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
}
