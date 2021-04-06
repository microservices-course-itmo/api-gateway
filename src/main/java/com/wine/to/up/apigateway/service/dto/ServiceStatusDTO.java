package com.wine.to.up.apigateway.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServiceStatusDTO {
    private String serviceName;
    private Boolean isActive;
}
