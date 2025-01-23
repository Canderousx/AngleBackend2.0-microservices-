package com.mailService.app.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic restorePasswordMailTopic(){
        return TopicBuilder.name("restore_password_mail")
                .build();
    }

    @Bean
    public NewTopic emailConfirmationMailTopic(){
        return TopicBuilder.name("email_confirmation_mail")
                .build();
    }

    @Bean
    public NewTopic accountBannedMailTopic(){
        return TopicBuilder.name("account_banned_mail")
                .build();
    }

    @Bean
    public NewTopic accountUnbannedMailTopic(){
        return TopicBuilder.name("account_unbanned_mail")
                .build();
    }




}