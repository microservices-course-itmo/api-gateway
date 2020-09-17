package com.wine.to.up.apigateway.service.controller;

import com.wine.to.up.apigateway.service.dto.ApiGatewayDto;
import com.wine.to.up.demo.catalog.service.api.feign.DemoCatalogServiceClient;
import com.wine.to.up.demo.service.api.feign.DemoServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiGatewayController {
    private final DemoCatalogServiceClient catalogService;

    private final DemoServiceClient demoService;

    @PostMapping("/composition/{messageId}")
    public void sendMessageToKafka(@PathVariable Integer messageId) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> catalogService.getAllMessages().get(messageId + 1))
                .thenAccept(demoService::sendMessage);

        future.get();
    }

    @PostMapping("/multiple/{messageId}")
    public void printAndSend(@PathVariable Integer messageId) throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> catalogService.printMessages(messageId)),
                CompletableFuture.runAsync(() -> demoService.sendMessage("Print id " + messageId + " was submitted"))
        ).get();
    }

    @GetMapping("aggregation")
    public ApiGatewayDto getAll() throws ExecutionException, InterruptedException {
      return CompletableFuture.supplyAsync(catalogService::getAllMessages)
                .thenCombineAsync(CompletableFuture.supplyAsync(demoService::getSentMessages),
                        ApiGatewayDto::new).get();
    }
}
