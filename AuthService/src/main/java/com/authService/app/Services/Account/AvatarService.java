package com.authService.app.Services.Account;

import com.authService.app.Models.Account;
import com.authService.app.Models.EnvironmentVariables;
import com.authService.app.Repositories.AccountRepository;
import com.authService.app.Services.Account.Interfaces.AvatarServiceInterface;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AvatarService implements AvatarServiceInterface {

    private final EnvironmentVariables environmentVariables;

    private final Logger logger = LogManager.getLogger(AvatarService.class);

    public AvatarService(EnvironmentVariables environmentVariables,
                         AccountRepository accountRepository) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public boolean checkExtension(MultipartFile file) {
        for (String extension : allowedExtensions) {
            if (file.getOriginalFilename().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkExtension(File file) {
        for(String extension : allowedExtensions){
            if(file.getName().endsWith(extension)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String saveAvatarFile(String userId,MultipartFile file){
        String filename = userId+".png";
        Path destinationFile = Paths
                .get(environmentVariables.getAvatarsPath())
                .resolve(Paths.get(filename))
                .normalize()
                .toAbsolutePath();
        try {
            file.transferTo(destinationFile);
            return filename;

        } catch (IOException e) {
            logger.error("Error during saving avatar file: "+filename);
            throw new RuntimeException(e);
        }
    }
}
