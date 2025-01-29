package com.statsService.app.Config.Init;

import com.statsService.app.Config.Exceptions.MissingEnvException;
import com.statsService.app.Models.EnvironmentVariables;
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
                .apiNinjaKey(System.getenv("API_NINJA_KEY"))
                .build();
        if(!env.checkIfNotNull()){
            throw new MissingEnvException("SOME OF YOUR SYSTEM VARIABLES CANNOT BE FOUND. PLEASE CHECK IT OUT IMMEDIATELY!");
        }
        logger.info("System variables loaded successfully");
        return env;
    }
}
