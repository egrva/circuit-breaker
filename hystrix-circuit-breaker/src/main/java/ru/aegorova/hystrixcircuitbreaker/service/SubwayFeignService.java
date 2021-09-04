package ru.aegorova.hystrixcircuitbreaker.service;

import feign.FeignException;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import ru.aegorova.hystrixcircuitbreaker.config.HystrixFeignBuilderHystrix;
import ru.aegorova.hystrixcircuitbreaker.dto.SubwayStationDto;


@FeignClient(value = "subway-service-circuit-breaker",
             url = "http://localhost:8080",
             fallbackFactory = SubwayFeignServiceFallbackFactory.class,
             configuration = HystrixFeignBuilderHystrix.class)
public interface SubwayFeignService {

    @GetMapping("/subway/stations/exception")
    SubwayStationDto getAllStationsException();

}

@Component
class SubwayFeignServiceFallbackFactory implements FallbackFactory<SubwayFeignService> {

    @Override
    public SubwayFeignService create(Throwable cause) {
        return new SubwayFeignServiceFallback(cause);
    }
}

@Slf4j
class SubwayFeignServiceFallback implements SubwayFeignService {

    private final Throwable cause;

    public SubwayFeignServiceFallback(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public SubwayStationDto getAllStationsException() {
        if (cause instanceof FeignException) {
            log.error("feign");
        } else {
            log.error(cause.getMessage());
        }
        return null;
    }

}