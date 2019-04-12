package com.gosuncn.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;

/**
 * Spring Cloud Gateway
 * https://cloud.spring.io/spring-cloud-gateway/spring-cloud-gateway.html
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
