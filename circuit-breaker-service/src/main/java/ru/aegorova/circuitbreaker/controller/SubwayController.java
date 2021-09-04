package ru.aegorova.circuitbreaker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aegorova.circuitbreaker.dto.SubwayStationDto;
import ru.aegorova.circuitbreaker.service.SubwayFeignService;

@RestController
@RequiredArgsConstructor
public class SubwayController {

    private final SubwayFeignService subwayFeignService;

    @GetMapping("/subway-info/stations/exception")
    public SubwayStationDto getAllStationsException() {
        return subwayFeignService.getAllStationsException();
    }

    @GetMapping("/subway-info/stations/timeout")
    public SubwayStationDto getAllStationsTimeout() {
        return subwayFeignService.getAllStationsTimeout();
    }

}
