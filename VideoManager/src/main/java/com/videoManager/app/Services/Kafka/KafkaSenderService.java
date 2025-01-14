package com.videoManager.app.Services.Kafka;


import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaSenderService {

    private final KafkaTemplate<String,String>kafkaTemplate;


    public KafkaSenderService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


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
