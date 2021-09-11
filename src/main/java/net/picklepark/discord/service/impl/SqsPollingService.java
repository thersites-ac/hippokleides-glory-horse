package net.picklepark.discord.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.audio.impl.QueueAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;
import net.picklepark.discord.service.PollingService;
import net.picklepark.discord.service.StorageService;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.model.S3Event;
import net.picklepark.discord.model.S3EventNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SqsPollingService implements PollingService {

    private static final String url = "https://sqs.us-east-2.amazonaws.com/166605477498/TrimmedRecordingQueue";
    private static final Logger logger = LoggerFactory.getLogger(SqsPollingService.class);

    private final SqsAsyncClient client;
    private final ReceiveMessageRequest request;

    private final StorageService storageService;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, Function<AudioContext, DiscordCommand>> clips;

    private int polls;

    public SqsPollingService(StorageService storageService) {
        clips = new ConcurrentHashMap<>();
        objectMapper = new ObjectMapper();
        client = SqsAsyncClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .region(Region.US_EAST_2)
                .httpClientBuilder(NettyNioAsyncHttpClient.builder()
                        .connectionMaxIdleTime(Duration.ofSeconds(20))
                        .connectionTimeout(Duration.ofSeconds(20)))
                .build();
        request = ReceiveMessageRequest.builder()
                .queueUrl(url)
                .waitTimeSeconds(20)
                .build();
        this.storageService = storageService;
        polls = 0;
    }

    @Override
    public void expect(String key) {
        resetPollCount();
        recursivelyExpect(key);
    }

    private void recursivelyExpect(String key) {
        if (polls <= 0) return;

        polls--;
        logger.info("Polling for {}: {} polls left", key, polls);
        client.receiveMessage(request).thenAccept(response -> {
            List<Message> messages = response.messages();
            processAll(messages);
            recursivelyExpect(key);
        });
    }

    private void processAll(List<Message> messages) {
        for (Message m: messages)
            process(m);
    }

    private void process(Message message) {
        String body = message.body();
        logger.info("Processing {}", body);
        S3EventNotification notification;
        try {
            notification = objectMapper.readValue(body, S3EventNotification.class);
            for (S3Event event: notification.getRecords())
                process(event);
        } catch (JsonProcessingException | URISyntaxException e) {
            e.printStackTrace();
        }

        DeleteMessageRequest delete = DeleteMessageRequest.builder()
                .receiptHandle(message.receiptHandle())
                .queueUrl(url)
                .build();
        client.deleteMessage(delete);
        logger.info("Deleted {}", message.receiptHandle());
    }

    private void process(S3Event event) throws URISyntaxException {
        String bucketName = event.getS3().getBucket().getName();
        String objectKey = event.getS3().getObject().getKey();
        LocalClip clip = storageService.download(bucketName, objectKey);
        Function<AudioContext, DiscordCommand> command = context -> makeCommand(clip.getPath(), context);
        clips.put(clip.getTitle(), command);
    }

    private DiscordCommand makeCommand(String path, AudioContext context) {
        return new QueueAudioCommand(path, context);
    }

    private void resetPollCount() {
        polls = 6;
    }

    @Override
    public DiscordCommand lookup(String command, AudioContext context) {
        if (clips.get(command) == null)
            return null;
        return clips.get(command).apply(context);
    }

}
