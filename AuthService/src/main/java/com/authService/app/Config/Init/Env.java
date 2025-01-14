package com.authService.app.Config.Init;

import com.authService.app.Config.Exceptions.MissingEnvException;
import com.authService.app.Models.EnvironmentVariables;
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
                .mtAdminName(System.getenv("ANGLE_ADMIN_NAME"))
                .mtAdminEmail(System.getenv("ANGLE_ADMIN_EMAIL"))
                .mtAdminPassword(System.getenv("ANGLE_ADMIN_PASSWORD"))
                .mtFrontUrl(System.getenv("MT_FRONT_URL"))
                .avatarsPath(System.getenv("ANGLE_AVATARS_PATH"))
                .build();
        if(!env.checkIfNotNull()){
            throw new MissingEnvException("SOME OF YOUR SYSTEM VARIABLES CANNOT BE FOUND. PLEASE CHECK IT OUT IMMEDIATELY!");
        }
        logger.info("System variables loaded successfully");
        return env;
    }
}
