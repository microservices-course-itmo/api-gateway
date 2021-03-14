package com.wine.to.up.apigateway.service.ribbon;

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerListFilter;

import java.util.*;
import java.lang.Override;


public class ServerListFilterCustom implements ServerListFilter<Server> {

    @Override
    public List<Server> getFilteredListOfServers(List<Server> servers) {
        List<Server> output = new ArrayList<Server>();
        for (Server server : servers) {
            if (server.getMetaInfo().getInstanceId().endsWith("dev")) {
                output.add(server);
            }
        }
        return output;
    }

}
