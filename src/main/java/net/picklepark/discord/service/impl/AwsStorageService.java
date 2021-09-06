package net.picklepark.discord.service.impl;

import net.picklepark.discord.service.StorageService;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;

public class AwsStorageService implements StorageService {

    private static final String DEFAULT_BUCKET = "discord-recordings";

    private final String bucket;
    private final S3Client client;
    private final S3Presigner presigner;

    public AwsStorageService(String bucket) {
        this.bucket = bucket;
        client = S3Client.builder()
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
    public URL store(File file) {
        String key = upload(file);
        return presingedUrlFor(key);
    }

    private String upload(File file) {
        String key = file.getName();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .storageClass(StorageClass.STANDARD)
                .build();

        client.putObject(request, Path.of(file.toURI()));
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
