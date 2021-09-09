package net.picklepark.discord.service.impl;

import net.picklepark.discord.exception.NotFoundException;
import net.picklepark.discord.service.StorageService;
import net.picklepark.discord.service.model.Coordinates;
import net.picklepark.discord.service.model.LocalClip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

public class AwsStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(AwsStorageService.class);
    private static final String DEFAULT_BUCKET = "discord-recordings";

    private final String bucket;
    private final S3Client downloadClient;
    private final S3Presigner presigner;
    private final S3Client storageClient;

    public AwsStorageService(String bucket) {
        this.bucket = bucket;
        downloadClient = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        storageClient = S3Client.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        presigner = S3Presigner.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    public AwsStorageService() {
        this(DEFAULT_BUCKET);
    }

    @Override
    public Coordinates store(File file) {
        String key = upload(file);
        URL url = presingedUrlFor(key);
        return Coordinates.builder()
                .key(key)
                .url(url)
                .build();
    }

    @Override
    public LocalClip download(String bucketName, String objectKey) throws URISyntaxException {
        logger.info("Checking to download {}/{}", bucketName, objectKey);
        GetObjectTaggingRequest taggingRequest = GetObjectTaggingRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        GetObjectTaggingResponse taggingResponse = downloadClient.getObjectTagging(taggingRequest);

        Optional<String> maybeTitle = taggingResponse.tagSet().stream()
                .filter(t -> t.key().equals("title"))
                .findFirst()
                .map(Tag::value);

        if (maybeTitle.isPresent()) {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
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

        throw new NotFoundException(objectKey, "s3://" + bucketName + "/" + objectKey);
    }

    private String upload(File file) {
        String key = file.getName();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .storageClass(StorageClass.STANDARD)
                .build();

        storageClient.putObject(request, Path.of(file.toURI()));
        return key;
    }

    private URL presingedUrlFor(String key) {
        GetObjectRequest basicRequest = GetObjectRequest.builder()
                .bucket(bucket)
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
