package com.videoManager.app.Services.Videos.Interfaces;

import com.videoManager.app.Config.Exceptions.FileServiceException;
import com.videoManager.app.Config.Exceptions.FileStoreException;
import com.videoManager.app.Config.Exceptions.ThumbnailsNotReadyYetException;
import com.videoManager.app.Models.Records.VideoProcessingData;
import com.videoManager.app.Models.ThumbnailsData;
import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoUploadInterface {

    void uploadVideo(MultipartFile file) throws BadRequestException, FileStoreException, FileServiceException, InterruptedException;

    void finishProcessing(VideoProcessingData vpf);

    void thumbnailsGeneratedProcess(ThumbnailsData data);

    List<String> getThumbnails(String videoId) throws ThumbnailsNotReadyYetException;
}
