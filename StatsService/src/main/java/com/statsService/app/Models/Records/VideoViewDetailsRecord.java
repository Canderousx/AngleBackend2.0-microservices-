package com.statsService.app.Models.Records;

import com.statsService.app.Models.Projections.LocationViewCount;

public record VideoViewDetailsRecord(
        String location,
        Long views
) implements LocationViewCount {
    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public Long getViewCount() {
        return views;
    }
}
