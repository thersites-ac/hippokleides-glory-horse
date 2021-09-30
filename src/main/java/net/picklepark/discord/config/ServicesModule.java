package net.picklepark.discord.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.picklepark.discord.service.PollingService;
import net.picklepark.discord.service.RecordingService;
import net.picklepark.discord.service.StorageService;
import net.picklepark.discord.service.impl.AwsStorageService;
import net.picklepark.discord.service.impl.LocalRecordingService;
import net.picklepark.discord.service.impl.SqsPollingService;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import javax.inject.Named;
import javax.inject.Singleton;
import java.time.Duration;

public class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(StorageService.class).to(AwsStorageService.class);
        bind(PollingService.class).to(SqsPollingService.class);
        bind(RecordingService.class).to(LocalRecordingService.class);
    }

    @Provides
    @Singleton
    private AwsCredentialsProvider credentialsProvider() {
       return ProfileCredentialsProvider.create();
    }

    @Provides
    @Named("download")
    @Singleton
    private S3Client downloadS3Client(AwsCredentialsProvider provider) {
        return S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(provider)
                .build();
    }

    @Provides
    @Named("storage")
    @Singleton
    private S3Client storageS3Client(AwsCredentialsProvider provider) {
        return S3Client.builder()
                .credentialsProvider(provider)
                .build();
    }

    @Provides
    @Singleton
    private S3Presigner presigner(AwsCredentialsProvider provider) {
        return S3Presigner.builder()
                .credentialsProvider(provider)
                .build();
    }

    @Provides
    @Singleton
    private SqsAsyncClient sqsClient(AwsCredentialsProvider provider) {
        return SqsAsyncClient.builder()
                .credentialsProvider(provider)
                .region(Region.US_EAST_2)
                .httpClientBuilder(NettyNioAsyncHttpClient.builder()
                        .connectionMaxIdleTime(Duration.ofSeconds(20))
                        .connectionTimeout(Duration.ofSeconds(20)))
                .build();
    }

    // FIXME: move to a properties file
    @Provides
    @Named("sqs.url")
    @Singleton
    private String sqsUrl() {
        return "https://sqs.us-east-2.amazonaws.com/166605477498/TrimmedRecordingQueue";
    }
}
