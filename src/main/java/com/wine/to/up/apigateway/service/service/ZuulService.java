package com.wine.to.up.apigateway.service.service;

import com.wine.to.up.apigateway.service.repository.UserToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ZuulService {
    private final UserToken userToken;

    public boolean containsToken(String token){
        return userToken.getTokens().contains(token);
    }

    public void addToken(String token){
        userToken.getTokens().add(token);
    }
}
