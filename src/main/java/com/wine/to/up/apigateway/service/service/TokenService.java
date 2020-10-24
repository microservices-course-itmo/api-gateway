package com.wine.to.up.apigateway.service.service;

import com.wine.to.up.apigateway.service.feign.ServiceFeignClient;
import com.wine.to.up.apigateway.service.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@RequiredArgsConstructor
@Service
public class TokenService {
    private final UserTokenRepository userTokenRepository;

    private final ServiceFeignClient serviceFeignClient;

    public boolean containsToken(String token){
        return userTokenRepository.containsToken(token);
    }

    public void addToken(String token){
        userTokenRepository.addToken(token);
    }

    public void sendValidateTokenRequestToUserService(String token){
        ResponseEntity<Void> responseEntity = serviceFeignClient.validate(token);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void removeExpiredTokens(){
        userTokenRepository.clearTokens();
    }
}
