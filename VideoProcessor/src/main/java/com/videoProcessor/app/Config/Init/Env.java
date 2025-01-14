package com.videoProcessor.app.Config.Init;

import com.videoProcessor.app.Config.Exceptions.MissingEnvException;
import com.videoProcessor.app.Models.EnvironmentVariables;
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
                .hlsOutputPath(System.getenv("ANGLE_HLS_FILES_PATH"))
                .ffmpegPath(System.getenv("FFMPEG_PATH"))
                .build();
        if(!env.checkIfNotNull()){
            throw new MissingEnvException("SOME OF YOUR SYSTEM VARIABLES CANNOT BE FOUND. PLEASE CHECK IT OUT IMMEDIATELY!");
        }
        logger.info("System variables loaded successfully");
        return env;
    }
}
