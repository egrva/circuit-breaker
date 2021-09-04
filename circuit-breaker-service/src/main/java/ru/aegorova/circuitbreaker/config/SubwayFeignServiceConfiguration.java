package ru.aegorova.circuitbreaker.config;

import feign.Feign;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.aegorova.circuitbreaker.service.FallbackSubwayFeignService;
import ru.aegorova.circuitbreaker.service.SubwayFeignService;

@RequiredArgsConstructor
@Configuration
public class SubwayFeignServiceConfiguration {

    private final CircuitBreakerRegistry registry;
    private final FallbackSubwayFeignService fallbackSubwayFeignService;

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(SubwayFeignService.SERVICE_NAME);
        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .withFallback(fallbackSubwayFeignService)
                .build();
        return Resilience4jFeign.builder(decorators);
    }
}

