package com.videoManager.app.Services.Videos.Interfaces;

import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Models.Video;

import java.io.IOException;
import java.util.List;

public interface VideoThumbnailsInterface {

    void processThumbnail(Video video) throws IOException, ClassNotFoundException, MediaNotFoundException;

    void processThumbnails(List<Video> toProcess);
}
