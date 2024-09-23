package com.example.compass.listener;

import com.example.compass.dto.PaymentDTO;
import com.example.compass.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Service
public class SqsMessageListener {
    private final SqsClient sqsClient;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;
    private final String partialQueueUrl = "partialPaymentQueue";
    private final String totalQueueUrl = "totalPaymentQueue";
    private final String excessQueueUrl = "excessPaymentQueue";

    @Autowired
    public SqsMessageListener(SqsClient sqsClient, PaymentService paymentService) {
        this.sqsClient = sqsClient;
        this.paymentService = paymentService;
        this.objectMapper = new ObjectMapper();
    }

    @Scheduled(fixedRate = 5000) // Poll every 5 seconds
    public void listenPartialPayments() {
        listenToQueue(partialQueueUrl);
    }

    @Scheduled(fixedRate = 5000) // Poll every 5 seconds
    public void listenTotalPayments() {
        listenToQueue(totalQueueUrl);
    }

    @Scheduled(fixedRate = 5000) // Poll every 5 seconds
    public void listenExcessPayments() {
        listenToQueue(excessQueueUrl);
    }

    private void listenToQueue(String queueUrl) {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10) // Number of messages to retrieve
                .waitTimeSeconds(20) // Long polling
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

        for (Message message : messages) {
            processMessage(message, queueUrl);
            // Optionally delete the message after processing
        }
    }

    private void processMessage(Message message, String queueUrl) {
        PaymentDTO paymentDTO = parseMessageToPaymentDTO(message);

        // Log the message processing
        System.out.println("Processing message: " + message.body());

        // Save or update the payment in the database
        paymentService.savePayment(paymentDTO);

        // After processing, delete the message from the queue
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }

    private PaymentDTO parseMessageToPaymentDTO(Message message) {
        try {
            return objectMapper.readValue(message.body(), PaymentDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse message to PaymentDTO", e);
        }
    }
}
