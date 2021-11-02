package net.picklepark.discord.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.service.ClipManager;
import net.picklepark.discord.service.RemoteStorageService;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.model.S3Event;
import net.picklepark.discord.model.S3EventNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Singleton
public class SqsPollingWorker extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(SqsPollingWorker.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final SqsClient client;
    private final ReceiveMessageRequest request;

    private final RemoteStorageService remoteStorageService;
    private final ClipManager commandManager;

    private final String url;
    private final long interval;

    @Inject
    public SqsPollingWorker(RemoteStorageService remoteStorageService,
                            SqsClient client,
                            ClipManager commandManager,
                            @Named("sqs.url") String url,
                            @Named("sqs.poll.interval") long interval) {
        this.client = client;
        this.remoteStorageService = remoteStorageService;
        this.commandManager = commandManager;
        this.url = url;
        this.interval = interval;
        request = ReceiveMessageRequest.builder()
                .queueUrl(url)
                .waitTimeSeconds(20)
                .build();
    }

    @Override
    public void run() {
        logger.info("Starting to poll...");
        while (true) {
            ReceiveMessageResponse response = client.receiveMessage(request);
            List<Message> messages = response.messages();
            processAll(messages);
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                logger.warn("Interrupted while waiting between polls", e);
            }
        }
    }

    @Override
    public void interrupt() {
        logger.info("Stopping polling...");
        super.interrupt();
    }

    private void processAll(List<Message> messages) {
        for (Message m: messages)
            process(m);
    }

    private void process(Message message) {
        String body = message.body();
        logger.info("Processing {}", body);
        try {
            S3EventNotification notification = objectMapper.readValue(body, S3EventNotification.class);
            for (S3Event event: notification.getRecords())
                process(event);
            DeleteMessageRequest delete = DeleteMessageRequest.builder()
                    .receiptHandle(message.receiptHandle())
                    .queueUrl(url)
                    .build();
            client.deleteMessage(delete);
            logger.info("Deleted {}", message.receiptHandle());
        } catch (JsonProcessingException | URISyntaxException | ResourceNotFoundException e) {
            logger.error("While processing message", e);
        }
    }

    private void process(S3Event event) throws URISyntaxException, ResourceNotFoundException {
        // in future, perhaps should confirm that the bucket name matches the expected bucket
//        String bucketName = event.getS3().getBucket().getName();
        String objectKey = event.getS3().getObject().getKey();
        try {
            LocalClip clip = remoteStorageService.download(objectKey);
            commandManager.put(clip);
        } catch (IOException e) {
            logger.error("While downloading clip", e);
            // FIXME: ideally we'd notify of failure, too
        }
    }

}
