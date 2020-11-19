package com.wine.to.up.apigateway.service.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.wine.to.up.apigateway.service.jwt.JwtTokenProvider;
import com.wine.to.up.apigateway.service.logging.GatewayNotableEvents;
import com.wine.to.up.apigateway.service.repository.UserTokenRepository;
import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.user.service.api.feign.AuthenticationServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@RequiredArgsConstructor
@Component
@Slf4j
public class CheckTokenFilter extends ZuulFilter {

    @SuppressWarnings("unused")
    @InjectEventLogger
    private EventLogger eventLogger;

    private final UserTokenRepository userTokenRepository;

    private final AuthenticationServiceClient authenticationServiceClient;


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
        log.info("Hello Julia");
        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        try {
            String accessToken = request.getHeader("accessToken");
            if (accessToken.equals("123")) {
                log.info("Default header is set");
                return null;
            }

            if (userTokenRepository.containsToken(accessToken)) {
                if (JwtTokenProvider.getExpirationDate(accessToken).after(new Date())) {
                    addHeaders(context, accessToken);
                    return null;
                } else {
                    log.info("Token " + accessToken + " is expired");
                    userTokenRepository.clearToken(accessToken);
                }
            }
            authenticationServiceClient.validate(accessToken);
            addHeaders(context, accessToken);
            userTokenRepository.addToken(accessToken);
        } catch (Exception e) {
            log.error("User is unauthorized");
            context.unset();
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null;

    }

    private void addHeaders(RequestContext context, String accessToken) {
        String role = JwtTokenProvider.getRole(accessToken);
        String id = JwtTokenProvider.getId(accessToken);
        String date = JwtTokenProvider.getExpirationDate(accessToken).toString();
        context.addZuulRequestHeader("id", id);
        context.addZuulRequestHeader("role", role);
        context.addZuulRequestHeader("expirationDate", date);
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
