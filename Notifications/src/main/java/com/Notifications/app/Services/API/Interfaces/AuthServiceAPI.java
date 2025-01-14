package com.Notifications.app.Services.API.Interfaces;

import org.springframework.data.domain.Page;

public interface AuthServiceAPI {

    Page<String>getSubscribedIds(String token,int page, int pageSize);


}
