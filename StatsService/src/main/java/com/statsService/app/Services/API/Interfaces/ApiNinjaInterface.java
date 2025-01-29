package com.statsService.app.Services.API.Interfaces;

public interface ApiNinjaInterface {

    String geoLocationApiAddress = "https://api.api-ninjas.com/v1/iplookup";

    String getLocation(String ipAddress);
}
