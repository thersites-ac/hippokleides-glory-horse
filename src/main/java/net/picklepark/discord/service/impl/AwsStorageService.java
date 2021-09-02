package net.picklepark.discord.service.impl;

import net.picklepark.discord.service.StorageService;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

public class AwsStorageService implements StorageService {

    private static final String DEFAULT_BUCKET = "discord-recordings";

    private final String bucket;
    private final S3Client client;

    public AwsStorageService(String bucket) {
        this.bucket = bucket;
        client = S3Client.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    public AwsStorageService() {
        this(DEFAULT_BUCKET);
    }

    @Override
    public URI store(File file) {
        String key = file.getName();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .storageClass(StorageClass.STANDARD)
                .build();

        client.putObject(request, Path.of(file.toURI()));

        return URI.create("https://" + bucket + ".s3.amazonaws.com/" + key);
    }

}
