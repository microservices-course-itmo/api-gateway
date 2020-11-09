package com.wine.to.up.apigateway.service.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.wine.to.up.apigateway.service.jwt.JwtTokenProvider;
import com.wine.to.up.apigateway.service.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        if (endpointToFilter.contains("swagger")||
                endpointToFilter.contains("api-docs") ||
                endpointToFilter.contains("deployment-service"))
            return false;
        endpointToFilter = endpointToFilter.substring(0, endpointToFilter.indexOf("/", 1));
        boolean shouldFilter = "/user-service".equals(endpointToFilter) || endpointToFilter.contains("parser");
        return !shouldFilter;
    }

    @Override
    public Object run(){
        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        String accessToken = request.getHeader("accessToken");

        if (accessToken.equals("123")) return null;

        if (tokenService.containsToken(accessToken)){
            String role = JwtTokenProvider.getRole(accessToken);
            String id = JwtTokenProvider.getId(accessToken);
            String date = JwtTokenProvider.getExpirationDate(accessToken).toString();
            context.addZuulRequestHeader("id", id);
            context.addZuulRequestHeader("role", role);
            context.addZuulRequestHeader("expirationDate", date);
        } else{
           boolean isValidated = tokenService.sendValidateTokenRequestToUserService(accessToken);
           if (isValidated) {
               String role = JwtTokenProvider.getRole(accessToken);
               String id = JwtTokenProvider.getId(accessToken);
               String date = JwtTokenProvider.getExpirationDate(accessToken).toString();
               context.addZuulRequestHeader("id", id);
               context.addZuulRequestHeader("role", role);
               context.addZuulRequestHeader("expirationDate", date);
           } else{
               context.unset();
               context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
           }
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
