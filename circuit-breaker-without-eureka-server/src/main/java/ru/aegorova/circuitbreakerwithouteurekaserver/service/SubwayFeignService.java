package ru.aegorova.circuitbreakerwithouteurekaserver.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.aegorova.circuitbreakerwithouteurekaserver.dto.SubwayStationDto;


@FeignClient(value="subway-service", url="http://localhost:8080")
public interface SubwayFeignService {

    @GetMapping("/subway/stations/exception")
    SubwayStationDto getAllStationsException();

}
