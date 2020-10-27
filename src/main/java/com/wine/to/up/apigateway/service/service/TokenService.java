package com.wine.to.up.apigateway.service.service;

import com.wine.to.up.apigateway.service.feign.ServiceFeignClient;
import com.wine.to.up.apigateway.service.repository.UserTokenRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpException;
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

    public boolean sendValidateTokenRequestToUserService(String token){
        try {
            ResponseEntity<Void> responseEntity = serviceFeignClient.validate(token);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                addToken(token);
                return true;
            }
        } catch (FeignException e) {
            return false;
        }
        return false;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void removeExpiredTokens(){
        userTokenRepository.clearTokens();
    }
}
