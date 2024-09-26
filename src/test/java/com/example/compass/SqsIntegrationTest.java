package com.example.compass;

import com.example.compass.config.AwsSqsConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(classes = {AwsSqsConfig.class})
public class SqsIntegrationTest {

//
//    @Autowired
//    private QueueMessagingTemplate queueMessagingTemplate;
//
//    @Value("${events.partial}")
//    private String partialPaymentQueue;
//
//    @Test
//    public void testSendAndReceiveMessage() throws InterruptedException {
//        // Send a test message
//        String testMessage = "Hello, SQS!";
//        queueMessagingTemplate.convertAndSend(partialPaymentQueue, testMessage);
//
//        // Setup a latch to wait for the message to be processed
//        CountDownLatch latch = new CountDownLatch(1);
//        TestMessageReceiver receiver = new TestMessageReceiver(latch, partialPaymentQueue); // Pass the queue name
//
//        // Wait for the message to be processed
//        latch.await();
//
//        // Assert that the message was received correctly
//        assertThat(receiver.getReceivedMessage()).isEqualTo(testMessage);
//    }
//
//    @Component
//    public static class TestMessageReceiver {
//
//        private String receivedMessage;
//
//        public TestMessageReceiver(CountDownLatch latch, String queueName) {
//            // Register message handler
//            // You might need a way to register the message handler properly in your configuration
//            this.messageHandler(queueName); // Pass the queue name
//        }
//
//        public void messageHandler(String message) {
//            this.receivedMessage = message;
//            // Release the latch if needed
//        }
//
//        public String getReceivedMessage() {
//            return receivedMessage;
//        }
//    }
}
