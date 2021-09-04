package ru.aegorova.circuitbreakerwithouteurekaserver.service;

import feign.FeignException;
import feign.RequestLine;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.aegorova.circuitbreakerwithouteurekaserver.dto.SubwayStationDto;


public interface SubwayFeignClientWithCircuitBreaker {

    @RequestLine("GET /subway/stations/exception")
    SubwayStationDto getAllStationsException();
}

@Component
@RequiredArgsConstructor
@Slf4j
class SubwayFeignClientWithCircuitBreakerConfig {

    private final CircuitBreakerRegistry registry;

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom().build())
                .timeLimiterConfig(TimeLimiterConfig.custom().build())
                .build());
    }

    @Bean
    SubwayFeignClientWithCircuitBreaker subwayFeignClientWithCircuitBreaker() {
        CircuitBreaker circuitBreaker = registry.circuitBreaker("backend");

        SubwayFeignClientWithCircuitBreaker requestFailedFallback = () -> {
            log.error("exception");
            return null;
        };
        SubwayFeignClientWithCircuitBreaker circuitBreakerFallback = () -> {
            log.error("fucking circuit");
            return null;
        };

        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .withFallback(requestFailedFallback, FeignException.class)
                .withFallback(circuitBreakerFallback, CallNotPermittedException.class)
                .build();

        return Resilience4jFeign.builder(decorators)
                .target(SubwayFeignClientWithCircuitBreaker.class, "http://localhost:8080/");
    }

}



