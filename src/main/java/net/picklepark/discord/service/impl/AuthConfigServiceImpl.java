package net.picklepark.discord.service.impl;

import net.picklepark.discord.service.AuthConfigService;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

@Singleton
public class AuthConfigServiceImpl implements AuthConfigService {

    private static final String CONFIG_KEY = "discord-bot-auth-config";
    private static final String TMP_ADMINS = "/tmp/discord-bot-auth-config";
    private final S3Client client;
    private final String configBucket;
    private final GetObjectRequest getConfigRequest;
    private final PutObjectRequest putObjectRequest;

    @Inject
    public AuthConfigServiceImpl(@Named("s3.bucket.config") String configBucket,
                                 @Named("s3.client.config") S3Client configFetcher) {
        this.client = configFetcher;
        this.configBucket = configBucket;
        getConfigRequest = GetObjectRequest.builder()
                .bucket(configBucket)
                .key(CONFIG_KEY)
                .build();
        putObjectRequest = PutObjectRequest.builder()
                .bucket(configBucket)
                .key(CONFIG_KEY)
                .build();
    }

    @Override
    public Map<String, Set<Long>> getCurrentAdmins() throws IOException {
        try (ResponseInputStream<GetObjectResponse> response = client.getObject(getConfigRequest)) {
            ObjectInputStream inputStream = new ObjectInputStream(response);
            return (Map<String, Set<Long>>) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void persistAdmins(Map<String, Set<Long>> admins) throws IOException {
        writeAsFile(admins);
        client.putObject(putObjectRequest, Path.of(TMP_ADMINS));
    }

    private void writeAsFile(Map<String, Set<Long>> admins) throws IOException {
        File file = new File(TMP_ADMINS);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file, false));
        out.writeObject(admins);
    }
}
