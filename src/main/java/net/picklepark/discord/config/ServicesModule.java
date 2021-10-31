package net.picklepark.discord.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.picklepark.discord.service.DynamicCommandManager;
import net.picklepark.discord.service.RecordingService;
import net.picklepark.discord.service.RemoteStorageService;
import net.picklepark.discord.service.impl.AwsRemoteStorageService;
import net.picklepark.discord.service.impl.DynamicCommandManagerImpl;
import net.picklepark.discord.service.impl.RecordingServiceImpl;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.inject.Named;
import javax.inject.Singleton;
import java.time.Duration;

public class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(RemoteStorageService.class).to(AwsRemoteStorageService.class);
        bind(RecordingService.class).to(RecordingServiceImpl.class);
        bind(DynamicCommandManager.class).to(DynamicCommandManagerImpl.class);
    }

    @Provides
    @Singleton
    AwsCredentialsProvider credentialsProvider() {
       return ProfileCredentialsProvider.create();
    }

    @Provides
    @Singleton
    SqsClient sqsClient () {
        return SqsClient.builder().region(Region.US_EAST_2).build();
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
                .credentialsProvider(provider)
                .build();
    }

    @Provides
    @Singleton
    S3Presigner presigner(AwsCredentialsProvider provider) {
        return S3Presigner.builder()
                .credentialsProvider(provider)
                .build();
    }

    // FIXME: move to a properties file
    @Provides
    @Named("sqs.url")
    String sqsUrl() {
        return "https://sqs.us-east-2.amazonaws.com/166605477498/TrimmedRecordingQueue";
    }

    @Provides
    @Named("sqs.poll.interval")
    long sqsPollInterval() {
        return 5000;
    }

    @Provides
    @Named("s3.uploads.bucket")
    String s3UploadsBucket() {
        return "discord-recordings";
    }

    @Provides
    @Named("s3.trimmed.bucket")
    String s3TrimmedBucket() {
        return "discord-output";
    }

    @Provides
    @Named("recording.clip.duration")
    int recordingClipDuration() {
        return 30;
    }

    @Provides
    @Named("s3.uploads.ttl")
    Duration timeToLive() {
        return Duration.ofMinutes(10);
    }
}
