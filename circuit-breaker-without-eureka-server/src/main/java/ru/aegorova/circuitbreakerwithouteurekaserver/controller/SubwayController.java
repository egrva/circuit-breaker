package ru.aegorova.circuitbreakerwithouteurekaserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aegorova.circuitbreakerwithouteurekaserver.dto.SubwayStationDto;
import ru.aegorova.circuitbreakerwithouteurekaserver.service.SubwayFeignClientWithCircuitBreaker;
import ru.aegorova.circuitbreakerwithouteurekaserver.service.SubwayFeignService;

@RestController
@RequiredArgsConstructor
public class SubwayController {

    private final SubwayFeignService subwayFeignService;
    private final SubwayFeignClientWithCircuitBreaker subwayFeignClientWithCircuitBreaker;

    @GetMapping("/subway-info/stations/exception")
    public SubwayStationDto getAllStationsException() {
        return subwayFeignService.getAllStationsException();
    }

    @GetMapping("/subway-info/stations/exception/circuit-breaker")
    public SubwayStationDto getAllStationsExceptionCircuitBreaker() {
        return subwayFeignClientWithCircuitBreaker.getAllStationsException();
    }

}
