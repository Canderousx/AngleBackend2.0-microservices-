package com.videoManager.app.Config.Init;

import com.videoManager.app.Config.Exceptions.MissingEnvException;
import com.videoManager.app.Models.EnvironmentVariables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Env {

    private final Logger logger = LogManager.getLogger(Env.class);

    @Bean
    EnvironmentVariables environmentVariables(){
        logger.info("Checking required System Environment Variables");
        EnvironmentVariables env = EnvironmentVariables.builder()
                .jTokenKey(System.getenv("JTOKEN_KEY"))
                .rawFilesPath(System.getenv("ANGLE_RAW_FILES_PATH"))
                .angleFrontUrl(System.getenv("ANGLE_FRONT_URL"))
                .thumbnailsPath(System.getenv("ANGLE_THUMBNAILS_PATH"))
                .hlsPath(System.getenv("ANGLE_HLS_FILES_PATH"))
                .build();
        System.out.println("THUMBNAILS PATH: "+env.getThumbnailsPath());
        if(!env.checkIfNotNull()){
            throw new MissingEnvException("SOME OF YOUR SYSTEM VARIABLES CANNOT BE FOUND. PLEASE CHECK IT OUT IMMEDIATELY!");
        }
        logger.info("System variables loaded successfully");
        return env;
    }
}
