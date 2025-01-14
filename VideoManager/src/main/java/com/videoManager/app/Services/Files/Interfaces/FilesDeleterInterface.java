package com.videoManager.app.Services.Files.Interfaces;


import com.videoManager.app.Config.Exceptions.FileServiceException;
import com.videoManager.app.Models.Video;

public interface FilesDeleterInterface {

    void deleteVideoFiles(Video video) throws FileServiceException;




}
