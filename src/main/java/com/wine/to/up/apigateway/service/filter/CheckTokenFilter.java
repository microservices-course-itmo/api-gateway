package com.wine.to.up.apigateway.service.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.wine.to.up.apigateway.service.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@RequiredArgsConstructor
@Component
public class CheckTokenFilter extends ZuulFilter {
    private final TokenService tokenService;

    @Override
    public boolean shouldFilter() {
        String endpointToFilter = RequestContext.getCurrentContext().getRequest().getRequestURI();
        endpointToFilter = endpointToFilter.substring(0, endpointToFilter.indexOf("/", 1));
        boolean shouldFilter = "/user-service".equals(endpointToFilter);
        return !shouldFilter;
    }

    @Override
    public Object run(){
        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        String accessToken = request.getHeader("accessToken");

        if (tokenService.containsToken(accessToken)){
            return null;
        } else{
           tokenService.sendValidateTokenRequestToUserService(accessToken);
        }
        //TODO: remove return
        return null;

    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
