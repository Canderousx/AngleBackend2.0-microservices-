package com.videoManager.app.Services.API.Interfaces;

import org.springframework.data.domain.Page;

import java.util.List;

public interface AuthServiceAPI {

    List<String> getRandomSubscribedIds(String token, int quantity);


}
