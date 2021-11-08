package net.picklepark.discord.service.impl;

import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.service.ClipManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
public class AwsRemoteStorageService implements RemoteStorageService {

    private static final Logger logger = LoggerFactory.getLogger(AwsRemoteStorageService.class);

    private final S3Client trimmedClipsClient;
    private final S3Presigner presigner;
    private final S3Client untrimmedClipsClient;
    private final String uploadsBucket;
    private final String clipsBucket;
    private final ClipManager commandManager;
    private final Map<String, String> remoteKeys;
    private final Duration timeToLive;
    private final String clipsDirectory;

    @Inject
    public AwsRemoteStorageService(@Named("s3.client.download") S3Client downloadClient,
                                   @Named("s3.client.upload") S3Client storageClient,
                                   @Named("s3.uploads.bucket") String uploadsBucket,
                                   @Named("s3.trimmed.bucket") String clipsBucket,
                                   @Named("s3.uploads.ttl") Duration timeToLive,
                                   @Named("clips.directory") String clipsDirectory,
                                   S3Presigner presigner,
                                   ClipManager commandManager) {
        this.uploadsBucket = uploadsBucket;
        this.clipsBucket = clipsBucket;
        this.trimmedClipsClient = downloadClient;
        this.untrimmedClipsClient = storageClient;
        this.presigner = presigner;
        this.commandManager = commandManager;
        this.timeToLive = timeToLive;
        this.clipsDirectory = clipsDirectory;
        remoteKeys = new HashMap<>();
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
    public LocalClip download(String objectKey) throws ResourceNotFoundException, IOException {
        Optional<String> maybeTitle = readTitle(objectKey);
        if (maybeTitle.isPresent())
            return downloadAs(objectKey, maybeTitle.get());
        else
            throw new ResourceNotFoundException(objectKey, "s3://" + clipsBucket + "/" + objectKey);
    }

    private LocalClip downloadAs(String objectKey, String title) throws IOException {
        remoteKeys.put(title, objectKey);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(clipsBucket)
                .key(objectKey)
                .build();

        logger.info("Downloading {}", title);
        String path = localPathOf(objectKey);
        InputStream inputStream = trimmedClipsClient.getObject(request);
        Files.copy(inputStream, Path.of(path));
        inputStream.close();
        return LocalClip.builder()
                .path(path)
                .title(title)
                .build();
    }

    private Optional<String> readTitle(String objectKey) {
        logger.info("Checking to download {}/{}", clipsBucket, objectKey);
        GetObjectTaggingRequest taggingRequest = GetObjectTaggingRequest.builder()
                .bucket(clipsBucket)
                .key(objectKey)
                .build();
        GetObjectTaggingResponse taggingResponse = trimmedClipsClient.getObjectTagging(taggingRequest);

        return taggingResponse.tagSet().stream()
                .filter(t -> t.key().equals("title"))
                .findFirst()
                .map(Tag::value);
    }

    @Override
    public void sync() {
        ListObjectsRequest request = ListObjectsRequest.builder()
                .bucket(clipsBucket)
                .build();
        ListObjectsResponse response = trimmedClipsClient.listObjects(request);
        downloadAll(response.contents());
    }

    @Override
    public void delete(String key) {
        String remoteKey = remoteKeys.get(key);
        logger.info("Deleting {} with AWS key {}", key, remoteKey);
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(clipsBucket)
                .key(remoteKey)
                .build();
        trimmedClipsClient.deleteObject(request);
        deleteLocal(remoteKey);
    }

    private void deleteLocal(String filename) {
        File localCopy = new File(localPathOf(filename));
        if (localCopy.exists())
            localCopy.delete();
    }

    private void downloadAll(List<S3Object> contents) {
        for (S3Object object: contents) {
            try {
                LocalClip clip = syncOneFile(object.key());
                commandManager.put(clip);
            } catch (ResourceNotFoundException | IOException e) {
                logger.error("While attempting to download " + object.key(), e);
            }
        }
    }

    private LocalClip syncOneFile(String key) throws ResourceNotFoundException, IOException {
        File file = new File(localPathOf(key));
        if (!file.exists())
            return download(key);
        else
            return nameLocalFile(key);
    }

    private LocalClip nameLocalFile(String key) throws ResourceNotFoundException {
        logger.info("{} already exists; looking up tagged title", key);
        String path = "clips/" + key;
        Optional<String> maybeTitle = readTitle(key);
        if (maybeTitle.isPresent()) {
            String title = maybeTitle.get();
            remoteKeys.put(title, key);
            return LocalClip.builder()
                    .path(path)
                    .title(title)
                    .build();
        }
        throw new ResourceNotFoundException(key, "s3://" + clipsBucket + "/" + key);

    }

    private String upload(File file) {
        String key = file.getName();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(uploadsBucket)
                .key(key)
                .storageClass(StorageClass.STANDARD)
                .build();
        untrimmedClipsClient.putObject(request, Path.of(file.toURI()));
        return key;
    }

    private URL presignedUrlFor(String key) {
        GetObjectRequest basicRequest = GetObjectRequest.builder()
                .bucket(uploadsBucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(timeToLive)
                .getObjectRequest(basicRequest)
                .build();

        PresignedGetObjectRequest output = presigner.presignGetObject(presignRequest);

        return output.url();
    }

    private String localPathOf(String objectKey) {
        return clipsDirectory + "/" + objectKey;
    }

}
