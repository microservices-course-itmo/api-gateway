package com.wine.to.up.apigateway.service.filter;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

@RequiredArgsConstructor
@Component
public class AddResponseHeadersFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @SneakyThrows
    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        context.addZuulResponseHeader("Access-Control-Allow-Origin", "*");
        context.addZuulResponseHeader("Access-Control-Allow-Headers", "Content-Type");
        context.addZuulResponseHeader("Access-Control-Allow-Methods", "OPTION, GET, POST, PUT, DELETE");
        return null;
    }
}
