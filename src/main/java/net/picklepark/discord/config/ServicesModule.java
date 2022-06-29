package net.picklepark.discord.config;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.picklepark.discord.adaptor.DataPersistenceAdaptor;
import net.picklepark.discord.adaptor.impl.DynamoPersistenceAdaptorImpl;
import net.picklepark.discord.model.AuthRecord;
import net.picklepark.discord.persistence.AuthRecordMappingFactory;
import net.picklepark.discord.persistence.MappingFactory;
import net.picklepark.discord.service.*;
import net.picklepark.discord.service.impl.*;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.inject.Named;
import javax.inject.Singleton;

import java.util.Map;
import java.util.Set;

import static net.picklepark.discord.constants.Names.*;

public class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ServiceConstantsModule());
        bind(RemoteStorageService.class).to(AwsRemoteStorageService.class);
        bind(RecordingService.class).to(RecordingServiceImpl.class);
        bind(ClipManager.class).to(ClipManagerImpl.class);
        bind(UrlShortener.class).to(BitlyUrlShortener.class);
        bind(WelcomeManager.class).to(WelcomeManagerImpl.class);
    }

    @Provides
    @Singleton
    AwsCredentialsProvider credentialsProvider() {
       return DefaultCredentialsProvider.create();
    }

    @Provides
    @Singleton
    SqsClient sqsClient () {
        return SqsClient.builder()
                .region(Region.US_EAST_2)
                .build();
    }

    @Provides
    @Named("s3.client.download")
    @Singleton
    S3Client downloadS3Client(AwsCredentialsProvider provider) {
        return S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(provider)
                .build();
    }

    @Provides
    @Named("s3.client.upload")
    @Singleton
    S3Client uploadS3Client(AwsCredentialsProvider provider) {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(provider)
                .build();
    }

    @Provides
    @Singleton
    @Named(S3_CLIENT_CONFIG)
    S3Client configS3Client(AwsCredentialsProvider provider) {
        return S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(provider)
                .build();
    }

    @Provides
    @Singleton
    S3Presigner presigner(AwsCredentialsProvider provider) {
        return S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(provider)
                .build();
    }

    @Provides
    @Singleton
    HttpRequestFactory requestFactory() {
        return new NetHttpTransport().createRequestFactory();
    }

    @Provides
    @Named(AUTH_BAN_PERSISTER)
    @Singleton
    JavaConfigManager<Map<String, Set<Long>>> banPersister(@Named(S3_BUCKET_CONFIG) String configBucket,
                                                           @Named(S3_CLIENT_CONFIG) S3Client configClient) {
        return new JavaConfigManager<Map<String, Set<Long>>>(configBucket, configClient, AUTH_BAN_PERSISTER);
    }

    @Provides
    @Singleton
    DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .build();
    }

    @Provides
    @Singleton
    DataPersistenceAdaptor<AuthRecord> dynamoAuthRecordPersistenceAdaptor(
            DynamoDbClient client,
            MappingFactory<AuthRecord> factory) {
        return new DynamoPersistenceAdaptorImpl<>(client, factory);
    }

    @Provides
    @Singleton
    MappingFactory<AuthRecord> authRecordMappingFactory() {
        return new AuthRecordMappingFactory();
    }
}
