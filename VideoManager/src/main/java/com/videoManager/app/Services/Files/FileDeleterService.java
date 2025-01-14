package com.videoManager.app.Services.Files;
import com.videoManager.app.Config.Exceptions.FileServiceException;
import com.videoManager.app.Models.EnvironmentVariables;
import com.videoManager.app.Models.Video;
import com.videoManager.app.Services.Files.Interfaces.FilesDeleterInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
@Slf4j
@RequiredArgsConstructor
public class FileDeleterService implements FilesDeleterInterface {

    private final EnvironmentVariables environmentVariables;


    private boolean deleteRawFiles(String path){
        File file = new File(path);
        if(!file.isFile()){
            log.error("Wrong raw file path. Aborting...");
            return false;
        }
        return file.delete();
    }

    private boolean deleteHlsFiles(String videoId, String playlistName) throws FileServiceException {
        File directory = new File(environmentVariables.getHlsPath()+File.separator+videoId);
        if(!directory.isDirectory()){
            log.error("Couldn't process provided path! Aborting operation");
            return false;
        }
        File[] files = directory.listFiles();
        if(files != null){
            for(File toRemove: files){
                log.info("Removing: "+toRemove.getName());
                if(toRemove.delete()){
                    log.info("File removed");
                }else{
                    log.error("Couldn't remove file: "+toRemove.getName());
                }
            }
            return directory.delete();
        }
        log.error("Files in dir: "+directory.getAbsolutePath()+" are null!");
        return false;
    }
    @Override
    public void deleteVideoFiles(Video video) throws FileServiceException {
        if(!deleteRawFiles(video.getRawPath())){
            log.error("Unable to delete raw files...");
            throw new FileServiceException("RAW FILES NOT FOUND: "+video.getRawPath());
        };
        if(!deleteHlsFiles(video.getId(),video.getPlaylistName())){
            log.error("Unable to delete hls files...");
            throw new FileServiceException("Unable to delete HLS files...");
        }

    }
}
