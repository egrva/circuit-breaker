package ru.aegorova.subwayduplicateservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aegorova.subwayduplicateservice.dto.SubwayStationDto;

import java.util.Arrays;

@RestController
@Slf4j
public class SubwayController {

    @GetMapping("/subway/stations/exception")
    public SubwayStationDto getAllStationsException() {
        throw new IllegalArgumentException("little exception:(");
    }

    @GetMapping("/subway/stations/timeout")
    public SubwayStationDto getAllStationsTimeout() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            log.error("wtf cant sleep...");
        }
        return SubwayStationDto.builder()
                .subwayStations(Arrays.asList("Кремлевская", "Площадь Тукая", "Суконная слобода", "Аметьево"))
                .build();
    }
}
