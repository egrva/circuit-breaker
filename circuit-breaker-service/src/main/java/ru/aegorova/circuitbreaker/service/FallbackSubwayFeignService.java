package ru.aegorova.circuitbreaker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.aegorova.circuitbreaker.dto.SubwayStationDto;

@Slf4j
@Component
public class FallbackSubwayFeignService implements SubwayFeignService {

    @Override
    public SubwayStationDto getAllStationsException() {
        log.error("exception");
        return null;
    }

    @Override
    public SubwayStationDto getAllStationsTimeout() {
        log.error("timeout");
        return null;
    }
}
