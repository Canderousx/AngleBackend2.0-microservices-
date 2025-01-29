package com.statsService.app.Services.API;

import com.statsService.app.Models.EnvironmentVariables;
import com.statsService.app.Services.API.Interfaces.ApiNinjaInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiNinjaService implements ApiNinjaInterface {

    private final RestClient restClient;

    private final EnvironmentVariables environmentVariables;
    @Override
    public String getLocation(String ipAddress) {
        Map<String,Object> data = restClient.get().uri(geoLocationApiAddress+"?address="+ipAddress)
                .header("X-Api-Key",environmentVariables.getApiNinjaKey())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (((request, response) -> {
                    log.error("Couldn't connect with Api Ninja: "+response.getStatusCode()+": "+response.getStatusText());
                }))).body(new ParameterizedTypeReference<Map<String, Object>>() {
                });
        String country = (String) data.get("country");
        if(country == null){
            log.warn("Warning: country received from api ninja is null!");
            return "undefined";
        }
        return country;
    }
}
