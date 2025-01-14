package com.videoManager.app.Services.API;

import com.videoManager.app.Models.Records.Account;
import com.videoManager.app.Services.API.Interfaces.AuthServiceAPI;
import com.videoManager.app.Services.Cache.PageWrapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class AuthServiceAPIService implements AuthServiceAPI {

    private final RestClient restClient;

    private final String ipaddress="http://localhost:7701";

    public AuthServiceAPIService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<String> getRandomSubscribedIds(String token, int quantity) {
        List<String> ids = restClient.get()
                .uri(ipaddress+"/accounts/getSubscribedChannelsRandom?quantity="+quantity)
                .header("Authentication","Bearer "+token)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (((request, response) -> {
                    throw new RuntimeException("Couldn't connect with the Auth Service!! RESPONSE CODE: "+response.getStatusCode());
                })))
                .body(new ParameterizedTypeReference<List<String>>() {
                });
        return ids;
    }
}
