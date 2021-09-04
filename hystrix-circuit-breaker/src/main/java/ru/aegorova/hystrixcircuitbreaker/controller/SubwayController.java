package ru.aegorova.hystrixcircuitbreaker.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aegorova.hystrixcircuitbreaker.dto.SubwayStationDto;
import ru.aegorova.hystrixcircuitbreaker.service.SubwayFeignService;
import ru.aegorova.hystrixcircuitbreaker.service.SubwayFeignServiceSimple;
import ru.aegorova.hystrixcircuitbreaker.service.SubwayFeignServiceSimpleFallback;


@RestController
@RequiredArgsConstructor
@Slf4j
public class SubwayController {

    private final SubwayFeignService subwayFeignService;
    private final SubwayFeignServiceSimple subwayFeignServiceSimple;
    private final SubwayFeignServiceSimpleFallback subwayFeignServiceSimpleFallback;

    @GetMapping("/subway-info/stations/exception")
    public SubwayStationDto getAllStationsException() {
        return subwayFeignService.getAllStationsException();
    }

    @GetMapping("/subway-info/stations/exception/simple")
    public SubwayStationDto getAllStationsExceptionSimple() {
        try {
            return subwayFeignServiceSimple.getAllStationsException();
        } catch (FeignException e) {
            log.error("feign Exception");
        }
        return null;
    }

    @GetMapping("/subway-info/stations/exception/simple/without-factory")
    public SubwayStationDto getAllStationsExceptionSimpleWithoutFactory() {
        return subwayFeignServiceSimpleFallback.getAllStationsException();
    }


}
