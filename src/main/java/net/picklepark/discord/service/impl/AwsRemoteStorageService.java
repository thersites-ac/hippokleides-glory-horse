package net.picklepark.discord.service.impl;

import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.audio.ClipCommand;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.service.DynamicCommandManager;
import net.picklepark.discord.service.RemoteStorageService;
import net.picklepark.discord.model.Coordinates;
import net.picklepark.discord.model.LocalClip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Singleton
public class AwsRemoteStorageService implements RemoteStorageService {

    private static final Logger logger = LoggerFactory.getLogger(AwsRemoteStorageService.class);

    private final S3Client downloadClient;
    private final S3Presigner presigner;
    private final S3Client storageClient;
    private final String uploadsBucket;
    private final String clipsBucket;
    private final DynamicCommandManager commandManager;

    @Inject
    public AwsRemoteStorageService(@Named("download") S3Client downloadClient,
                                   @Named("storage") S3Client storageClient,
                                   @Named("s3.uploads.bucket") String uploadsBucket,
                                   @Named("s3.trimmed.bucket") String clipsBucket,
                                   S3Presigner presigner,
                                   DynamicCommandManager commandManager) {
        this.uploadsBucket = uploadsBucket;
        this.clipsBucket = clipsBucket;
        this.downloadClient = downloadClient;
        this.storageClient = storageClient;
        this.presigner = presigner;
        this.commandManager = commandManager;
    }

    @Override
    public Coordinates store(File file) {
        String key = upload(file);
        URL url = presignedUrlFor(key);
        return Coordinates.builder()
                .key(key)
                .url(url)
                .build();
    }

    @Override
    public LocalClip download(String objectKey) throws ResourceNotFoundException {
        logger.info("Checking to download {}/{}", clipsBucket, objectKey);
        GetObjectTaggingRequest taggingRequest = GetObjectTaggingRequest.builder()
                .bucket(clipsBucket)
                .key(objectKey)
                .build();
        GetObjectTaggingResponse taggingResponse = downloadClient.getObjectTagging(taggingRequest);

        Optional<String> maybeTitle = taggingResponse.tagSet().stream()
                .filter(t -> t.key().equals("title"))
                .findFirst()
                .map(Tag::value);

        if (maybeTitle.isPresent()) {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(clipsBucket)
                    .key(objectKey)
                    .build();

            String title = maybeTitle.get();
            logger.info("Downloading {}", title);
            String path = "clips/" + objectKey;
            InputStream inputStream = downloadClient.getObject(request);
            try {
                Files.copy(inputStream, Path.of(path));
                logger.info("Downloaded it!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return LocalClip.builder()
                    .path(path)
                    .title(title)
                    .build();
        }

        throw new ResourceNotFoundException(objectKey, "s3://" + clipsBucket + "/" + objectKey);
    }

    @Override
    public void sync() {
        ListObjectsRequest request = ListObjectsRequest.builder()
                .bucket(clipsBucket)
                .build();
        ListObjectsResponse response = downloadClient.listObjects(request);
        downloadAll(response.contents());
    }

    private void downloadAll(List<S3Object> contents) {
        for (S3Object object: contents) {
            try {
                // FIXME: this logic should go in another class, probably the command manager class itself
                LocalClip clip = download(object.key());
                DiscordCommand command = new ClipCommand(clip.getPath());
                commandManager.put(clip.getTitle(), command);
            } catch (ResourceNotFoundException e) {
                logger.warn("Could not download {}", object.key());
            }
        }
    }

    private String upload(File file) {
        String key = file.getName();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(uploadsBucket)
                .key(key)
                .storageClass(StorageClass.STANDARD)
                .build();
        storageClient.putObject(request, Path.of(file.toURI()));
        return key;
    }

    private URL presignedUrlFor(String key) {
        GetObjectRequest basicRequest = GetObjectRequest.builder()
                .bucket(uploadsBucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(basicRequest)
                .build();

        PresignedGetObjectRequest output = presigner.presignGetObject(presignRequest);

        return output.url();
    }

}
