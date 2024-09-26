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

import java.util.List;

@Slf4j
@Component
public class SqsMessageListener {

    private static final Logger log = LoggerFactory.getLogger(SqsMessageListener.class);

    @Value("${sqsQueuePartial}")
    private String sqsQueuePartial;

    @Value("${sqsQueueFull}")
    private String sqsQueueFull;

    @Value("${sqsQueueExcess}")
    private String sqsQueueExcess;

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        log.info("Listeners initialized for Partial: {}, Full: {}, Excess: {}", sqsQueuePartial, sqsQueueFull, sqsQueueExcess);
    }

    @SqsListener("${sqsQueuePartial}")
    public void partialQueueListener(String message) {
        processMessage(message, "Partial");
    }

    @SqsListener("${sqsQueueFull}")
    public void fullQueueListener(String message) {
        processMessage(message, "Full");
    }

    @SqsListener("${sqsQueueExcess}")
    public void excessQueueListener(String message) {
        processMessage(message, "Excess");
    }

    private void processMessage(String message, String queueType) {
        try {
            // Log the received message
            log.info("Received {} payment message from the queue: {}", queueType, message);

            // Fetch the same message from the queue for verification
            String containerMessage = fetchMessageFromQueue(queueType);
            log.info("This {} message was sent to the queue: {}", queueType, containerMessage);

            // Deserialize the message to BatchPaymentMessage object
            BatchPaymentMessage batchPaymentMessage = objectMapper.readValue(message, BatchPaymentMessage.class);


        } catch (Exception ex) {
            log.error("Error processing {} payment message: {}", queueType, ex.getMessage());
        }
    }

    @Autowired
    private SqsClient sqsClient;

    private String fetchMessageFromQueue(String queueUrl) {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .waitTimeSeconds(5) // Optional: Long polling
                .build();

        // Fetch messages from the queue
        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

        // Check if there are any messages and return the body of the first one
        return messages.stream()
                .findFirst()
                .map(Message::body) // Use Message::body to get the body of the message
                .orElse(null);
    }
}
