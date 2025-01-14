package com.Notifications.app.Services.API;

import com.Notifications.app.Services.API.Interfaces.AuthServiceAPI;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
public class AuthServiceAPIService implements AuthServiceAPI {

    private final RestClient restClient;

    private final String ipaddress="http://localhost:7701";

    public AuthServiceAPIService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Page<String> getSubscribedIds(String token,int page, int pageSize) {
        Page<String>ids = restClient.get()
                .uri(ipaddress+"/accounts/getSubscribedIds?page="+page+"&pageSize="+pageSize)
                .header("Authentication","Bearer "+token)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (((request, response) -> {
                    throw new RuntimeException("Couldn't connect with the Auth Service!! RESPONSE CODE: "+response.getStatusCode());
                })))
                .body(new ParameterizedTypeReference<Page<String>>() {
                });
        return ids;
    }
}
