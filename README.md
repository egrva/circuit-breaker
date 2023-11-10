# Circuit breaker
This document covers various approaches to using the "Circuit Breaker" pattern, including solutions from ["Hystrix"](https://github.com/Netflix/Hystrix/wiki) and 
["Resilience4j"](https://resilience4j.readme.io/docs)

* [About pattern](#about-pattern)
* [Resilience4j solutions](#resilience4j-solutions)
* [Hystrix solutions](#hystrix-solutions)
* [Eureka naming server, Ribbon, Resilience4j, Feign](#eureka-naming-server-ribbon-resilience4j-feign)
* [Simple example with Feign](#simple-example-with-feign)
* [Hystrix and Feign](#hystrix-and-feign)
* [Used technologies](#used-technologies)

## About pattern
*Circuit breaker* is one of the patterns used in microservices architecture. It helps enhance the system's fault tolerance and is designed to handle less predictable errors such as network failures or unavailability of services. In such situations, the chances of encountering the same error again are high. For example, when an application interacts with a service, there is a timeout for requests and responses. If no response is received within this timeout, the operation is considered unsuccessful. In case of issues with this service, the application may consume critical resources (memory, processing time) while waiting for a response and before reaching the timeout. In such a situation, it is preferable for the application to terminate the operation with an error immediately, without waiting for the service timeout, and retry the attempt only when the probability of successful completion is sufficiently high.

The Circuit breaker pattern prevents the application from attempting an operation that is likely to fail, allowing it to continue working without consuming essential resources until the problem is resolved. The application should quickly acknowledge the operation failure and handle it. It also allows the application to determine whether the malfunction has been fixed. If the problem is resolved, the application can attempt to call the operation again.

A Circuit breaker can be in three states:
![circuit-breaker](images/circuit-breaker-pattern-01.png)
1. Closed: Application requests are directed to the operation. The proxy server counts the number of recent failures, and if the operation call is unsuccessful, the proxy server increments this number. If the number of recent failures exceeds a specified threshold within a specified time period, the proxy server transitions to the Open state. At this stage, the proxy server starts a timeout timer, and when this timer expires, the proxy server transitions to the Half-Open state. The purpose of this pattern is to give the system time to fix the error that caused the failure before allowing the application to attempt the operation again.

2. Open: Application requests are immediately terminated with an error, and an exception is returned to the application.

3. Half-Open: A limited number of requests from the application are allowed to pass through the operation and call it. If these requests are successful, it is assumed that the error that previously caused the failure has been fixed, and the automatic switch transitions to the Closed state (failure count is reset). If any request fails, the automatic switch assumes that the malfunction is still present, so it returns to the Open state and restarts the timeout timer to give the system additional time to recover after the failure.
The Half-Open state helps prevent a rapid increase in requests to the service. After the service starts working, for some time, it may be able to handle a limited number of requests before full recovery.

## Resilience4j solutions
[Resilience4j](https://resilience4j.readme.io/docs) - is a lightweight, easy-to-use library with a set of tools for improving fault tolerance. It replaces Netflix Hystrix (which, for those unaware, was discontinued in 2018). This library includes the following features:
* [Circuit breaker](https://resilience4j.readme.io/docs/circuitbreaker)
* [Bulkhead](https://resilience4j.readme.io/docs/bulkhead)
* [Rate Limiter](https://resilience4j.readme.io/docs/ratelimiter)
* [Retry](https://resilience4j.readme.io/docs/retry)
* [Time Limiter](https://resilience4j.readme.io/docs/timeout)
* [Cache](https://resilience4j.readme.io/docs/cache)

## Hystrix solutions
[Netflix Hystrix](https://github.com/Netflix/Hystrix/wiki) - s a library that helps control interactions between distributed services, adding resilience to delays and faults. Hystrix achieves this by isolating entry points between services, stopping cascading failures between them, and providing fallback options, all of which improve the overall fault tolerance of your system.
(As stated on their official page)

In addition, Hystrix has a cool feature - the Hystrix dashboard. It allows you to visualize metrics to understand system effectiveness.
![hystrix-dashboard](images/Hystrix-dashboard.png)
(This is what the dashboard looks like)

## Eureka naming server, Ribbon, Resilience4j, Feign
Let's move on to examples. In the first example, we will explore the interaction of the Circuit breaker with Feign, Eureka naming service, and Ribbon. This example will focus on the implementation from Resilience4j.
#### Subway service

As a third-party service, let's take the Subway-service. It processes two requests: one returns an error, and the other sleeps (this will be useful for testing timeouts).

```java
    @GetMapping("/subway/stations/exception")
    public SubwayStationDto getAllStationsException() {
        throw new IllegalArgumentException("little exception:(");
    }

    @GetMapping("/subway/stations/timeout")
    public SubwayStationDto getAllStationsTimeout() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            log.error("can't sleep...");
        }
        return SubwayStationDto.builder()
                .subwayStations(Arrays.asList("Кремлевская", "Площадь Тукая", "Суконная слобода", "Аметьево"))
                .build();
    }
```

To work with Eureka Naming Server, add:
Annotation on the main class:
````java
@EnableDiscoveryClient
@SpringBootApplication
public class SubwayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubwayServiceApplication.class, args);
	}
}
````
Dependency in pom.xml
````xml
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
````

In application.properties, specify the URL to Eureka server:

````properties
eureka.client.service-url.default-zone=http://localhost:8761/eureka
````

#### Eureka naming server
Configure the Eureka server (nothing interesting, just the default server).

*Main.class* 
````java
@EnableEurekaServer
@SpringBootApplication
public class EurekaNamingServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(EurekaNamingServiceApplication.class, args);
	}
}
````

*application.properties*
````properties
spring.application.name=netflix-eureka-naming-server
server.port=8761

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
````

#### Circuit breaker service
Hmm...)) This is what we gathered here for.

Let's dive into configuring the Feign Client.

Add the following annotations to Main.class:
````java
@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties
@EnableEurekaClient
@RibbonClient(name = "subway-service")
public class CircuitBreakerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CircuitBreakerApplication.class, args);
    }
}
````

*@EnableFeignClients* - for working with Feign clients
*@EnableEurekaClient* - for working with Eureka server
*@RibbonClient* - for working with Ribbon

To work with Circuit Breaker, we need to add a configuration class to the @FeignClient annotation.

````java
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
````

In the configuration class, declare Feign.Builder, and pass our Circuit Breaker and Fallback to FeignDecorator.

Fallback allows defining behavior when an error occurs.

````java
@RequiredArgsConstructor
@Configuration
public class SubwayFeignServiceConfiguration {

    private final CircuitBreakerRegistry registry;
    private final FallbackSubwayFeignService fallbackSubwayFeignService;

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(SubwayFeignService.SERVICE_NAME);
        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .withFallback(fallbackSubwayFeignService)
                .build();
        return Resilience4jFeign.builder(decorators);
    }
}

````

Also, it is necessary to configure the Circuit Breaker. I have placed all the settings in the application.properties file.
````properties
spring.application.name=subway-info-service
server.port=8081
eureka.client.service-url.default-zone=http://localhost:8761/eureka

ribbon.eureka.enabled=false
spring.cloud.loadbalancer.ribbon.enabled=false

resilience4j.circuitbreaker.configs.default.slowCallDurationThreshold=1000
resilience4j.circuitbreaker.configs.default.failureRateThreshold=50
resilience4j.circuitbreaker.configs.default.sliding-window-type=COUNT_BASED
resilience4j.circuitbreaker.configs.default.slidingWindowSize=3
resilience4j.circuitbreaker.configs.default.minimumNumberOfCalls=5
resilience4j.circuitbreaker.configs.default.waitDurationInOpenState=60s

resilience4j.circuitbreaker.configs.default.registerHealthIndicator=true
resilience4j.circuitbreaker.configs.default.permittedNumberOfCallsInHalfOpenState=1
resilience4j.circuitbreaker.configs.default.automaticTransitionFromOpenToHalfOpenEnabled=true
resilience4j.circuitbreaker.configs.default.eventConsumerBufferSize=10

feign.client.config.default.connectTimeout=60000
feign.client.config.default.readTimeout=1000
````

## Simple example with Feign

In this example, Eureka Naming Server and Ribbon will not be used. This example demonstrates how to configure some FeignClients using CircuitBreaker while others do not.

(circuit-breaker-without-eureka-server)

## Hystrix and Feign

This example shows how to use the Circuit Breaker from Netflix Hystrix (Remember, it's deprecated!).

(hystrix-circuit-breaker)

## Used technologies
* [Feign Clients](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
* [Eureka](https://github.com/Netflix/eureka)
* [Ribbon](https://github.com/Netflix/ribbon)
* [Resilience4j](https://resilience4j.readme.io/docs)
* [Hystrix](https://github.com/Netflix/Hystrix)
