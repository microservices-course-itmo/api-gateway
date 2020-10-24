package com.wine.to.up.apigateway.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class AuthenticationResponseModel {
    private String accessToken;
    private String refreshToken;

    private UserResponseModel user;
}
