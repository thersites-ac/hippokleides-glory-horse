package net.picklepark.discord.service.impl;

import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.service.StorageService;
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
import java.util.Optional;

@Singleton
public class AwsStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(AwsStorageService.class);

    private final S3Client downloadClient;
    private final S3Presigner presigner;
    private final S3Client storageClient;
    private final String uploadsBucket;
    private final String clipsBucket;

    @Inject
    public AwsStorageService(@Named("download") S3Client downloadClient,
                             @Named("storage") S3Client storageClient,
                             @Named("s3.uploads.bucket") String uploadsBucket,
                             @Named("s3.trimmed.bucket") String clipsBucket,
                             S3Presigner presigner) {
        this.uploadsBucket = uploadsBucket;
        this.clipsBucket = clipsBucket;
        this.downloadClient = downloadClient;
        this.storageClient = storageClient;
        this.presigner = presigner;
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
