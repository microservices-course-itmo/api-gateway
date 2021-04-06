package com.wine.to.up.apigateway.service.status;

import com.wine.to.up.apigateway.service.dto.ServiceStatusDTO;
import org.apache.commons.validator.UrlValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CallServices {
    private static final ArrayList<String> services = (ArrayList<String>) Arrays.asList(
            "http://proxy-service:8080",
            "http://catalog-service:8080",
            "http://user-service:8080",
            "http://ml-description-based-recommendation-service:5000",
            "http://ml-team-2-service:80",
            "http://ml3-recommendation-service:8000",
            "http://segandrec-service:5000",
            "http://notification-service:8080"
    );
    private static final String extractServiceNameRegExp = "http:\\/\\/([-\\w]+):[0-9]+";

    private String extractServiceName(String service) {
        Pattern pattern = Pattern.compile(extractServiceNameRegExp);
        Matcher matcher = pattern.matcher(service);
        return matcher.group(1);
    }

    private Boolean getStatus(String service) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(service);
    }

    public List<ServiceStatusDTO> checkServices() {
        List<ServiceStatusDTO> serviceStatusDTOS = new ArrayList<>();

        for(String service : services) {
            ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO();

            serviceStatusDTO.setServiceName(extractServiceName(service));
            serviceStatusDTO.setIsActive(getStatus(service));
            serviceStatusDTOS.add(serviceStatusDTO);
        }

        return serviceStatusDTOS;
    }
}
