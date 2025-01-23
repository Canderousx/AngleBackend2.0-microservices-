package com.reportService.app.Services.API;
import com.reportService.app.Services.API.Interfaces.AuthServiceAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthServiceAPIService implements AuthServiceAPI {

    private final RestClient restClient;

    private final String ipaddress="http://localhost:7701";

    @Override
    public String getUsername(String id) {
        return restClient.get()
                .uri(ipaddress+"/accounts/getUsername?id="+id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (((request, response) -> {
                    throw new RuntimeException("Couldn't connect with the Auth Service!! RESPONSE CODE: "+response.getStatusCode());
                })))
                .body(String.class);
    }
}
