package com.wine.to.up.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ApiGateWayDto {
    private final List<String> catalogMessages;
    private final List<String> allSentMessages;
}
