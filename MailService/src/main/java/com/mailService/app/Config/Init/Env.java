package com.mailService.app.Config.Init;

import com.mailService.app.Models.EnvironmentVariables;
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
                .angleEmailAddress(System.getenv("ANGLE_EMAIL_ADDRESS"))
                .angleEmailPassword(System.getenv("ANGLE_EMAIL_PASSWORD"))
                .angleFrontUrl(System.getenv("ANGLE_FRONT_URL"))
                .build();
        if(!env.checkIfNotNull()){
            throw new RuntimeException("SOME OF YOUR SYSTEM VARIABLES CANNOT BE FOUND. PLEASE CHECK IT OUT IMMEDIATELY!");
        }
        logger.info("System variables loaded successfully");
        return env;
    }
}
