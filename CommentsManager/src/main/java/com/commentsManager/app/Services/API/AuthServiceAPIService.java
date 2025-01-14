package com.commentsManager.app.Services.API;

import com.commentsManager.app.Models.Records.Account;
import com.commentsManager.app.Services.API.Interfaces.AuthServiceAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
@RequiredArgsConstructor
public class AuthServiceAPIService implements AuthServiceAPI {

    private final RestClient restClient;

    private final String ipaddress="http://localhost:7701";


    @Override
    @Cacheable(value = "account_details",key = "#id")
    public Account getAccountDetails(String id) {
        return restClient.get()
                .uri(ipaddress+"/accounts/getUserById?id="+id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (((request, response) -> {
                    throw new RuntimeException("Couldn't connect with the Auth Service!! RESPONSE CODE: "+response.getStatusCode());
                })))
                .body(Account.class);
    }
}
