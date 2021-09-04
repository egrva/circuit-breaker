package ru.aegorova.hystrixcircuitbreaker.service;

import feign.Feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import ru.aegorova.hystrixcircuitbreaker.config.DefaultFeignBuilder;
import ru.aegorova.hystrixcircuitbreaker.dto.SubwayStationDto;


@FeignClient(value="subway-service-simple",
             url="http://localhost:8080",
             configuration = DefaultFeignBuilder.class)
public interface SubwayFeignServiceSimple {

    @GetMapping("/subway/stations/exception")
    SubwayStationDto getAllStationsException();

}
