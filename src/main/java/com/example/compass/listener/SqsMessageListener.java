package com.example.compass.listener;

import com.example.compass.dto.BatchPaymentMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Slf4j
@Component
public class SqsMessageListener {

    private static final Logger log = LoggerFactory.getLogger(SqsMessageListener.class);

    // Queue names from application.properties
    @Value("${sqs.partial.queue}")
    private String sqsQueuePartialName;

    @Value("${sqs.full.queue}")
    private String sqsQueueFullName;

    @Value("${sqs.excess.queue}")
    private String sqsQueueExcessName;

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SqsClient sqsClient;


    @PostConstruct
    public void init() {

        log.info("Listeners initialized for Partial: {}, Full: {}, Excess: {}", sqsQueuePartialName, sqsQueueFullName, sqsQueueExcessName);
    }

    @SqsListener("${sqs.partial.queue}")
    public void partialQueueListener(String message) {
        processMessage(message, sqsQueuePartialName, "Partial");
    }

    @SqsListener("${sqs.full.queue}")
    public void fullQueueListener(String message) {
        processMessage(message, sqsQueueFullName, "Full");
    }

    @SqsListener("${sqs.excess.queue}")
    public void excessQueueListener(String message) {
        processMessage(message, sqsQueueExcessName, "Excess");
    }

    private void processMessage(String message, String queueName, String queueType) {
        try {
            String queueUrl = constructQueueUrl(queueName); // Construct the full queue URL
            log.info("Received {} payment message from the queue: {}", queueType, message);

            // Fetch the message from the actual queue URL
            Pair<String, String> containerMessage = fetchMessageFromQueue(queueUrl);
            String messageId = containerMessage.getLeft();
            String body = containerMessage.getRight();

            if (body != null) {
                log.info("This {} message was sent to the queue: {}", queueType, body);
                log.info("Message ID: {}", messageId);

                // Deserialize the message
                BatchPaymentMessage batchPaymentMessage = objectMapper.readValue(body, BatchPaymentMessage.class);
            } else {
                log.warn("No message body received for {} queue", queueType);
            }

        } catch (Exception ex) {
            log.error("Error processing {} payment message: {}", queueType, ex.getMessage());
        }
    }

    private String constructQueueUrl(String queueName) {
        // Construct the full queue URL based on the name
        return "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/" + queueName;
    }


    private Pair<String, String> fetchMessageFromQueue(String queueUrl) {
        log.info("Fetching message from queue URL: {}", queueUrl);

        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .waitTimeSeconds(5)
                .visibilityTimeout(0)
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

        if (messages.isEmpty()) {
            log.warn("No messages received from the queue: {}", queueUrl);
            return Pair.of(null, null);
        }

        Message message = messages.get(0);
        String messageId = message.messageId();
        String messageBody = message.body();
        return Pair.of(messageId, messageBody); // Return a pair of MessageId and body
    }
}
