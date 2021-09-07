package net.picklepark.discord.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.service.PollingService;
import net.picklepark.discord.service.StorageService;
import net.picklepark.discord.service.model.S3EventNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SqsPollingService implements PollingService {

    private static final String url = "https://sqs.us-east-2.amazonaws.com/166605477498/TrimmedRecordingQueue";
    private static final Logger logger = LoggerFactory.getLogger(SqsPollingService.class);

    private final SqsClient client;
    private final ReceiveMessageRequest request;

    private final Poller poller;
    private final StorageService storageService;
    private final Set<String> expectedKeys;

    public SqsPollingService(StorageService storageService) {
        client = SqsClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .region(Region.US_EAST_2)
                .httpClientBuilder(ApacheHttpClient.builder()
                        .connectionMaxIdleTime(Duration.ofSeconds(20))
                        .connectionTimeout(Duration.ofSeconds(22)))
                .build();
        request = ReceiveMessageRequest.builder()
                .queueUrl(url)
                .waitTimeSeconds(20)
                .build();
        expectedKeys = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.storageService = storageService;
        poller = new Poller();
    }


    @Override
    public void expect(String key) {
        expectedKeys.add(key);
        if (pollerNeedsStart())
            poller.start();
    }

    private boolean pollerNeedsStart() {
        return !poller.isAlive() || poller.isInterrupted();
    }

    @Override
    public DiscordCommand lookup(String command) {
        return null;
    }

    private class Poller extends Thread {

        @Override
        public void run() {
            while (!expectedKeys.isEmpty())
                poll();
            interrupt();
        }

        private void poll() {
            logger.info("Polling for new messages");
            ReceiveMessageResponse response = client.receiveMessage(request);
            for (Message message: response.messages()) {
                process(message);
                delete(message);
            }
        }

        private void process(Message message) {
            logger.info(message.body());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                S3EventNotification notification = objectMapper.readValue(message.body(), S3EventNotification.class);
                logger.info("Bucket: {}, key: {}",
                        notification.getRecords().get(0).getS3().getBucket().getName(),
                        notification.getRecords().get(0).getS3().getObject().getKey());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        private void delete(Message message) {
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .receiptHandle(message.receiptHandle())
                    .queueUrl(url)
                    .build();
            client.deleteMessage(deleteRequest);
        }

    }
}
