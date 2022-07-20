package cogbog.discord.worker;

import cogbog.discord.adaptor.Messager;
import cogbog.discord.model.CanonicalKey;
import cogbog.discord.model.LocalClip;
import cogbog.discord.service.ClipManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cogbog.discord.service.RemoteStorageService;
import cogbog.discord.model.S3Event;
import cogbog.discord.model.S3EventNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import javax.inject.Named;
import java.util.List;

public class SqsPollingWorker extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(SqsPollingWorker.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final SqsClient client;
    private final ReceiveMessageRequest request;

    private final RemoteStorageService remoteStorageService;
    private final ClipManager clipManager;
    private final Messager messager;

    private final String url;

    public SqsPollingWorker(RemoteStorageService remoteStorageService,
                            SqsClient client,
                            ClipManager clipManager,
                            Messager messager,
                            @Named("sqs.url") String url,
                            @Named("sqs.poll.duration") int duration) {
        super("NewClipNotifications");
        this.client = client;
        this.remoteStorageService = remoteStorageService;
        this.clipManager = clipManager;
        this.url = url;
        this.messager = messager;
        request = ReceiveMessageRequest.builder()
                .queueUrl(url)
                .waitTimeSeconds(duration)
                .build();
    }

    @Override
    public void run() {
        logger.info("Starting to poll...");
        while (true) {
            ReceiveMessageResponse response = client.receiveMessage(request);
            List<Message> messages = response.messages();
            processAll(messages);
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
        } catch (JsonProcessingException e) {
            logger.error("While processing message", e);
        }
    }

    private void process(S3Event event) {
        // in future, perhaps should confirm that the bucket name matches the expected bucket
//        String bucketName = event.getS3().getBucket().getName();
        String objectKey = event.getS3().getObject().getKey();
        LocalClip clip = null;
        try {
            clip = remoteStorageService.download(CanonicalKey.fromString(objectKey));
            clipManager.put(clip);
            if (clip.getMetadata() != null && clip.getMetadata().getOriginatingTextChannel() > 0) {
                messager.send(
                        clip.getGuild(),
                        clip.getMetadata().getOriginatingTextChannel() + "",
                        "I know how to " + clip.getTitle()
                );
            }
        } catch (Exception e) {
            logger.error("While downloading clip", e);
        }
    }

}
