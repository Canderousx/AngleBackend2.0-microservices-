package com.thumbnailGenerator.app.Services.FFMpeg.Interfaces;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface FFMpegDataRetrievalInterface {

    double getVideoDuration(String rawPath) throws IOException, InterruptedException;

    CompletableFuture<List<String>> generateVideoThumbnails(String videoId, String rawPath) throws IOException, InterruptedException;
}
