package com.videoProcessor.app.Services.FFMpeg.Interfaces;

import java.util.concurrent.CompletableFuture;

public interface FFMpegConverterInterface {

    CompletableFuture<Void> convertToHls(String filePath, String videoId);


}
