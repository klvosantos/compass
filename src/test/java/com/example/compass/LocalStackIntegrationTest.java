package com.example.compass;

import com.example.compass.dto.PaymentDTO; // Ensure this import is correct
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@SpringBootTest
public class LocalStackIntegrationTest {

    private static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(SQS);

    private SqsClient sqsClient;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        localstack.start();
        sqsClient = SqsClient.builder()
                .endpointOverride(localstack.getEndpointOverride(SQS))
                .region(Region.of(localstack.getRegion()))
                .build();

        createQueues();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private void createQueues() {
        // Create the necessary SQS queues
        sqsClient.createQueue(CreateQueueRequest.builder().queueName("partialPaymentQueue").build());
        sqsClient.createQueue(CreateQueueRequest.builder().queueName("totalPaymentQueue").build());
        sqsClient.createQueue(CreateQueueRequest.builder().queueName("excessPaymentQueue").build());
    }

    @AfterEach
    public void tearDown() {
        localstack.stop();
    }

    // Test for FULL payment
    @Test
    public void testTotalPaymentProcessing() throws Exception {
        String requestBody = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 100.00 } ] }";

        mockMvc.perform(post("/batch-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments[0].status").value("FULL"));

        verifyQueueMessage("totalPaymentQueue", "FULL");
    }

    // Test for PARTIAL payment
    @Test
    public void testPartialPaymentProcessing() throws Exception {
        String requestBody = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 50.00 } ] }";

        mockMvc.perform(post("/batch-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments[0].status").value("PARTIAL"));

        verifyQueueMessage("partialPaymentQueue", "PARTIAL");
    }

    // Test for OVERPAID payment
    @Test
    public void testExcessPaymentProcessing() throws Exception {
        String requestBody = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 150.00 } ] }";

        mockMvc.perform(post("/batch-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments[0].status").value("OVERPAID"));

        verifyQueueMessage("excessPaymentQueue", "OVERPAID");
    }

    // Test for invalid seller
    @Test
    public void testInvalidSeller() throws Exception {
        String requestBody = "{ \"sellerId\": 99, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 50.00 } ] }";

        mockMvc.perform(post("/batch-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Seller not found"));
    }

    // Test for invalid billing code
    @Test
    public void testInvalidBillingCode() throws Exception {
        String requestBody = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 99, \"originalAmount\": 100.00, \"amount\": 50.00 } ] }";

        mockMvc.perform(post("/batch-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Billing code not found"));
    }

    // Test for malformed request
    @Test
    public void testMalformedRequest() throws Exception {
        String requestBody = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00 } ] }"; // Missing amount

        mockMvc.perform(post("/batch-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing payment amount"));
    }

    // Test for concurrent processing
    @Test
    public void testConcurrentPaymentProcessing() throws Exception {
        String requestBody1 = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 50.00 } ] }";
        String requestBody2 = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 150.00 } ] }";

        // Run both requests in parallel
        Thread thread1 = new Thread(() -> {
            try {
                mockMvc.perform(post("/batch-payment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody1))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.payments[0].status").value("PARTIAL"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                mockMvc.perform(post("/batch-payment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody2))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.payments[0].status").value("OVERPAID"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    private void verifyQueueMessage(String queueName, String expectedStatus) {
        List<Message> messages = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl(queueName)
                        .maxNumberOfMessages(10)
                        .build())
                .messages();

        assertFalse(messages.isEmpty());
        PaymentDTO paymentDTO = parseMessageToPaymentDTO(messages.get(0));
        assertEquals(expectedStatus, paymentDTO.getStatus());
    }

    private PaymentDTO parseMessageToPaymentDTO(Message message) {
        try {
            return new ObjectMapper().readValue(message.body(), PaymentDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse message to PaymentDTO", e);
        }
    }
}
