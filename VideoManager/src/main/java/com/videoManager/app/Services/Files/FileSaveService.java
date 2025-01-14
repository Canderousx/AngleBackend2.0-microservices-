package com.videoManager.app.Services.Files;


import com.videoManager.app.Config.Exceptions.FileStoreException;
import com.videoManager.app.Models.EnvironmentVariables;
import com.videoManager.app.Services.Files.Interfaces.FilesSaveInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileSaveService implements FilesSaveInterface {


    private final EnvironmentVariables environmentVariables;

    @Override
    public String saveRawFile(MultipartFile file) throws FileStoreException {
        if(file.isEmpty()){
            log.error("File sent to save is empty!");
            throw new FileStoreException("File sent to save is empty!");
        };
        String savedFileName = UUID.randomUUID()+"_"+UUID.randomUUID()+".mp4";
        Path destinationFile = Paths
                .get(environmentVariables.getRawFilesPath())
                .resolve(Paths.get(savedFileName))
                .normalize()
                .toAbsolutePath();
        if(!destinationFile.getParent().equals(Paths.get(environmentVariables.getRawFilesPath()).toAbsolutePath())){
            log.error("File cannot be saved outside rawFiles directory!");
            throw new FileStoreException("File cannot be saved outside rawFiles directory!");
        }
        try {
            file.transferTo(destinationFile);
            log.info("File has been saved!");
            return destinationFile.toString();
        } catch (IOException e) {
            log.error("Couldn't save the file...");
            throw new FileStoreException("File cannot be saved outside rawFiles directory!");
        }
    }
}
