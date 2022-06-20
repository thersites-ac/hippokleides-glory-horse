package net.picklepark.discord.service.impl;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.nio.file.Path;

// TODO (important): replace this with something scalable
public class JavaConfigManager<T> {

    private final S3Client client;
    private final GetObjectRequest getConfigRequest;
    private final PutObjectRequest putObjectRequest;
    private final String uri;

    public JavaConfigManager(String configBucket, S3Client configFetcher, String configKey) {
        this.client = configFetcher;
        getConfigRequest = GetObjectRequest.builder()
                .bucket(configBucket)
                .key(configKey)
                .build();
        putObjectRequest = PutObjectRequest.builder()
                .bucket(configBucket)
                .key(configKey)
                .build();
        this.uri = "/tmp/" + configKey;
    }

    public T getRemote() throws IOException {
        try (ObjectInputStream inputStream = new ObjectInputStream(client.getObject(getConfigRequest))) {
            return (T) inputStream.readObject();
        } catch (ClassNotFoundException | NoSuchKeyException e) {
            throw new IOException(e);
        }
    }

    public void persist(T object) throws IOException {
        writeAsFile(object);
        client.putObject(putObjectRequest, Path.of(uri));
    }

    private void writeAsFile(T object) throws IOException {
        File file = new File(uri);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file, false));
        out.writeObject(object);
        out.close();
    }
}
