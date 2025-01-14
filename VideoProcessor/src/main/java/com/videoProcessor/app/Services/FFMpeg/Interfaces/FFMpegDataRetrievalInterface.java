package com.videoProcessor.app.Services.FFMpeg.Interfaces;

import java.io.IOException;
import java.util.List;



public interface FFMpegDataRetrievalInterface {

    double getVideoDuration(String rawPath) throws IOException, InterruptedException;
}
