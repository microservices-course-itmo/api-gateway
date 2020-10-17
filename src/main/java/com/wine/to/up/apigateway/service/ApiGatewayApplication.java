package com.wine.to.up.apigateway.service;

import com.wine.to.up.apigateway.service.filter.CheckTokenFilter;
import com.wine.to.up.apigateway.service.filter.SaveTokenFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan("com.wine.to.up")
@EnableZuulProxy
@EnableSwagger2
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public CheckTokenFilter checkTokenZuulFilter(){
        return new CheckTokenFilter();
    }

    @Bean
    public SaveTokenFilter saveTokenZuulFilter(){
        return new SaveTokenFilter();
    }

}