package com.example.compass.listener;

import com.example.compass.dto.BatchPaymentMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Service
public class SqsProducer {
    private static final Logger log = LoggerFactory.getLogger(SqsProducer.class);

    @Value("${sqs.partial.queue}")
    private String SQSPartial;

    @Value("${sqs.full.queue}")
    private String SQSFull;

    @Value("${sqs.excess.queue}")
    private String SQSExcess;

    private SqsTemplate sqsTemplate;

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        log.info("Producer initialized for queues: Partial: {}, Full: {}, Excess: {}", SQSPartial, SQSFull, SQSExcess);
    }

    @Autowired
    public SqsProducer(SqsAsyncClient sqsAsyncClient, ObjectMapper objectMapper) {
        this.sqsTemplate = SqsTemplate.newTemplate(sqsAsyncClient);
        this.objectMapper = objectMapper;
    }

    public void sendMessage(BatchPaymentMessage message) {
        String queueUrl = resolveQueueUrl(message);

        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            log.info("Sending message to queue {}: {}", queueUrl, jsonMessage);
            this.sqsTemplate.send(queueUrl, jsonMessage);
        } catch (Exception e) {
            log.error("Failed to send message to queue {}: {}", queueUrl, e);
        }
    }

    private String resolveQueueUrl(BatchPaymentMessage message) {
        String status = message.getPayments().get(0).getStatus();
        switch (status) {
            case "PARTIAL":
                return SQSPartial;
            case "FULL":
                return SQSFull;
            case "OVERPAID":
                return SQSExcess;
            default:
                throw new IllegalArgumentException("Invalid queue type: " + status);
        }
    }
}
