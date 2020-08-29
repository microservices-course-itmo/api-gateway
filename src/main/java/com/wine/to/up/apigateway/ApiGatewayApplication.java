package com.wine.to.up.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.wine.to.up")
@EnableZuulProxy
public class ApiGatewayApplication {
//http://localhost:8081/service/swagger-ui.html#/kafka-controller
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}