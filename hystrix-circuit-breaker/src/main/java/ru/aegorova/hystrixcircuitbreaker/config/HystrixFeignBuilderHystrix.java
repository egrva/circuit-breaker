package ru.aegorova.hystrixcircuitbreaker.config;

import feign.hystrix.HystrixFeign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class HystrixFeignBuilderHystrix {
    @Bean
    @Scope("prototype")
    public HystrixFeign.Builder hystrixFeignBuilder() {
        return HystrixFeign.builder();
    }
}