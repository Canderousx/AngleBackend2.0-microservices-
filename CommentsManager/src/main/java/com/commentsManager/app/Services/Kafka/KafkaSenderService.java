package com.commentsManager.app.Services.Kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaSenderService {

    private final KafkaTemplate<String,String>kafkaTemplate;



    public void send(String topic, String value){
        CompletableFuture<Void> sender = kafkaTemplate.send(
                        topic,value
                ).thenAccept(result -> {
                    log.info("Message sent successfully. Topic: " + result.getRecordMetadata().topic()
                            + " Partition: " + result.getRecordMetadata().partition()
                            + " Offset: " + result.getRecordMetadata().offset());
                })
                .exceptionally(ex -> {
                    log.error("Failed to send message: " + ex.getMessage());
                    return null;
                });

    }
}
