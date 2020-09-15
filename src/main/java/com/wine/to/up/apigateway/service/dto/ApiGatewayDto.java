package com.wine.to.up.apigateway.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ApiGatewayDto {
    private final List<String> catalogMessages;
    private final List<String> sentMessages;
}
