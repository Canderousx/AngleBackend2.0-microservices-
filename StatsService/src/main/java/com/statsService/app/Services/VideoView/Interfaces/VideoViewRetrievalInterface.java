package com.statsService.app.Services.VideoView.Interfaces;

import com.statsService.app.Models.Records.VideoViewDetailsRecord;

import java.util.List;

public interface VideoViewRetrievalInterface {

    long countViews(String videoId);

    long countViews(String videoId,String location);

    List<VideoViewDetailsRecord>getViewsDescByLocation(String videoId);



}
