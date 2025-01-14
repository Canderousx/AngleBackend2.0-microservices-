package com.commentsManager.app.Services.API.Interfaces;

import com.commentsManager.app.Models.Records.Account;
import org.springframework.data.domain.Page;

public interface AuthServiceAPI {

    Account getAccountDetails(String id);

}
