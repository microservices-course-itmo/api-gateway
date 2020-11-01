package com.wine.to.up.apigateway.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://user-service:8080")
public interface ServiceFeignClient {

    @PostMapping("/validate")
    ResponseEntity<Void> validate(@RequestParam String token);
}
