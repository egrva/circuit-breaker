package ru.aegorova.hystrixcircuitbreaker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import ru.aegorova.hystrixcircuitbreaker.config.HystrixFeignBuilderHystrix;
import ru.aegorova.hystrixcircuitbreaker.dto.SubwayStationDto;

@FeignClient(value="subway-service-circuit-breaker-simple-fallback",
             url="http://localhost:8080",
             fallback = SubwaySimpleFallback.class,
             configuration = HystrixFeignBuilderHystrix.class)
public interface SubwayFeignServiceSimpleFallback {

    @GetMapping("/subway/stations/exception")
    SubwayStationDto getAllStationsException();
}

@Component
@Slf4j
class SubwaySimpleFallback implements SubwayFeignServiceSimpleFallback{

    @Override
    public SubwayStationDto getAllStationsException() {
        log.error("mistake...");
        return null;
    }
}