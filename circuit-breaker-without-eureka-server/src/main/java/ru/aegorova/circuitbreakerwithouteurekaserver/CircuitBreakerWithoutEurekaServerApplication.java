package ru.aegorova.circuitbreakerwithouteurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CircuitBreakerWithoutEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CircuitBreakerWithoutEurekaServerApplication.class, args);
	}

}
