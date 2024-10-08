package com.example.compass;


import org.apache.logging.log4j.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;




import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class LocalStackIntegrationTest {

//    private static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
//            .withServices(SQS);

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ConfigurableEnvironment environment; // Inject the environment

    private MockMvc mockMvc;

    private static final Logger logger = LoggerFactory.getLogger(LocalStackIntegrationTest.class);

    @BeforeEach
    public void setUp() {
       // localstack.start();

//        if (!localstack.isRunning()) {
//            throw new RuntimeException("LocalStack container failed to start");
//        }

        // Set AWS credentials in the Spring environment
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("AWS_ACCESS_KEY_ID", "test");
//        properties.put("AWS_SECRET_ACCESS_KEY", "test");
//        environment.getPropertySources().addFirst(new MapPropertySource("testProperties", properties));
//
//        // Set them as system properties as well
//        System.setProperty("AWS_ACCESS_KEY_ID", "test");
//        System.setProperty("AWS_SECRET_ACCESS_KEY", "test");
//
//        // Log the credentials to verify
//        System.out.println("*** AWS_ACCESS_KEY_ID: " + System.getProperty("AWS_ACCESS_KEY_ID"));
//        System.out.println("*** AWS_SECRET_ACCESS_KEY: " + System.getProperty("AWS_SECRET_ACCESS_KEY"));

        // Create SQS client
//        sqsClient = SqsClient.builder()
//                .endpointOverride(localstack.getEndpointOverride(SQS))
//                .region(Region.of(localstack.getRegion()))
//                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
//                .build();
//
//        System.out.println("SQS Client initialized: " + (sqsClient != null));
//
//        // Directly retrieve and print the access key
//        String accessKeyId = "test"; // Hardcoded since you're using test credentials
//        System.out.println("Using SQS Client with access key: " + accessKeyId);
//
//        // Verify LocalStack is configured correctly
//        System.out.println("verifyLocalStackConfiguration starts: ");
//        verifyLocalStackConfiguration();
//        System.out.println("verifyLocalStackConfiguration ends: ");
//
//        createQueues();
//
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();


    }



//    private void createQueues() {
//        try {
//            sqsClient.createQueue(CreateQueueRequest.builder().queueName("partial-queue").build());
//            sqsClient.createQueue(CreateQueueRequest.builder().queueName("full-queue").build());
//            sqsClient.createQueue(CreateQueueRequest.builder().queueName("excess-queue").build());
//        } catch (Exception e) {
//            System.out.println("Failed to create queues: " + e.getMessage());
//            throw new RuntimeException("Could not create SQS queues", e);
//        }
//
//        // List queues
//        ListQueuesResponse response = sqsClient.listQueues();
//        System.out.println("Queues: " + response.queueUrls());
//    }


//    @AfterEach
//    public void tearDown() {
//        localstack.stop();
//    }

    // Test for FULL payment // success
//    @Test
//    public void testTotalPaymentProcessing() throws Exception {
//        String requestBody = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 100.00 } ] }";
//
//        mockMvc.perform(post("/batch-payment")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.payments[0].status").value("FULL"));
//
//        System.out.println("Full payment ");
//
//        logger.info("Full payment");
//
//        //verifyQueueMessage("totalPaymentQueue", "FULL");
//    }

    // Test for PARTIAL payment // success
//    @Test
//    public void testPartialPaymentProcessing() throws Exception {
//        String requestBody = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 50.00 } ] }";
//
//        mockMvc.perform(post("/batch-payment")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.payments[0].status").value("PARTIAL"));
//
//        //verifyQueueMessage("partial-queue", "PARTIAL");
//    }


    // Test for OVERPAID payment // success
    @Test
    public void testExcessPaymentProcessing() throws Exception {
        String requestBody = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 150.00 } ] }";

        mockMvc.perform(post("/batch-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments[0].status").value("EXCESS"));

        //verifyQueueMessage("excess-queue", "OVERPAID");
    }
//
//    // Test for invalid seller // success
//    @Test
//    public void testInvalidSeller() throws Exception {
//        String requestBody = "{ \"sellerId\": 99, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 50.00 } ] }";
//
//        mockMvc.perform(post("/batch-payment")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.error").value("Seller not found"));
//    }
//
//    // Test for invalid billing code
//    @Test
//    public void testInvalidBillingCode() throws Exception {
//        String requestBody = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 99, \"originalAmount\": 100.00, \"amount\": 50.00 } ] }";
//
//        mockMvc.perform(post("/batch-payment")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.error").value("Billing code not found"));
//    }

    // Test for malformed request // sucesso
//    @Test
//    public void testMalformedRequest() throws Exception {
//        String requestBody = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00 } ] }"; // Missing amount
//
//        mockMvc.perform(post("/batch-payment")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Missing payment amount"));
//    }

    // Test for concurrent processing
//    @Test
//    public void testConcurrentPaymentProcessing() throws Exception {
//        String requestBody1 = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 50.00 } ] }";
//        String requestBody2 = "{ \"sellerId\": 1, \"payments\": [ { \"billingCodeId\": 1, \"originalAmount\": 100.00, \"amount\": 150.00 } ] }";
//
//        // Create and start both requests in parallel
//        Thread thread1 = new Thread(() -> {
//            try {
//                mockMvc.perform(post("/batch-payment")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(requestBody1))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.payments[0].status").value("PARTIAL"));
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        Thread thread2 = new Thread(() -> {
//            try {
//                mockMvc.perform(post("/batch-payment")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(requestBody2))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.payments[0].status").value("OVERPAID"));
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        thread1.start();
//        thread2.start();
//        thread1.join();
//        thread2.join();
//    }

//    private void verifyQueueMessage(String queueName, String expectedStatus) {
//        List<Message> messages = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
//                        .queueUrl(queueName)
//                        .maxNumberOfMessages(10)
//                        .build())
//                .messages();
//
//        assertFalse(messages.isEmpty());
//        PaymentDTO paymentDTO = parseMessageToPaymentDTO(messages.get(0));
//        assertEquals(expectedStatus, paymentDTO.getStatus());
//    }

//    private PaymentDTO parseMessageToPaymentDTO(Message message) {
//        try {
//            return new ObjectMapper().readValue(message.body(), PaymentDTO.class);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to parse message to PaymentDTO", e);
//        }
//    }
}
