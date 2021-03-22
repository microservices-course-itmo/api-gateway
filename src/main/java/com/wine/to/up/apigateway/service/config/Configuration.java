package com.wine.to.up.apigateway.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import org.modelmapper.ModelMapper;

@org.springframework.context.annotation.Configuration
public class Configuration {

    /**
     * Model mapper bean
     */
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    /**
     * Object mapper bean
     */
    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }


    @Bean
    public Docket gatewayApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.gatewayAPIInfo())
                .select()
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .build();

    }

    private ApiInfo gatewayAPIInfo() {
        return new ApiInfoBuilder()
                .title("API Gateway - build #1.0.1")
                .description("Enter the IDs in order to look for the content")
                .version("1.0.1")
                .build();
    }
}
