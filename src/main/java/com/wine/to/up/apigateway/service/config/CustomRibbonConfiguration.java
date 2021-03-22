package com.wine.to.up.apigateway.service.config;

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerListFilter;
import com.wine.to.up.apigateway.service.ribbon.ServerListFilterCustom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomRibbonConfiguration {

    @Bean
    public ServerListFilter<Server> serverListFilter() {
        return new ServerListFilterCustom();
    }

}
