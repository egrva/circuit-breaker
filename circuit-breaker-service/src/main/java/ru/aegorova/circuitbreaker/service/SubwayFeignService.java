package ru.aegorova.circuitbreaker.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.aegorova.circuitbreaker.config.SubwayFeignServiceConfiguration;
import ru.aegorova.circuitbreaker.dto.SubwayStationDto;


@FeignClient(value = SubwayFeignService.SERVICE_NAME,
             configuration = SubwayFeignServiceConfiguration.class)
public interface SubwayFeignService {

    String SERVICE_NAME = "subway-service";

    @GetMapping("/subway/stations/exception")
    @CircuitBreaker(name = SERVICE_NAME)
    SubwayStationDto getAllStationsException();

    @GetMapping("/subway/stations/timeout")
    @CircuitBreaker(name = SERVICE_NAME)
    SubwayStationDto getAllStationsTimeout();

}

