package com.thumbnailGenerator.app.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic thumbnailsGeneratedTopic(){
        return TopicBuilder.name("thumbnails_generated")
                .build();
    }

    @Bean
    public NewTopic thumbnailsGeneratorErrorTopic(){
        return TopicBuilder.name("thumbnails_generator_error")
                .build();
    }




}