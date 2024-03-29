package cogbog.discord.config;

import cogbog.discord.adaptor.DataPersistenceAdaptor;
import cogbog.discord.adaptor.Messager;
import cogbog.discord.adaptor.impl.DynamoPersistenceAdaptorImpl;
import cogbog.discord.adaptor.impl.MessagerImpl;
import cogbog.discord.model.Recording;
import cogbog.discord.model.WelcomeRecord;
import cogbog.discord.persistence.AuthRecordMappingFactory;
import cogbog.discord.persistence.MappingFactory;
import cogbog.discord.persistence.RecordingMappingFactory;
import cogbog.discord.service.*;
import cogbog.discord.service.impl.*;
import cogbog.discord.worker.SqsPollingWorker;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import cogbog.discord.model.AuthRecord;
import cogbog.discord.persistence.WelcomeRecordMappingFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.inject.Named;
import javax.inject.Singleton;

public class ServicesModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(ServicesModule.class);

    @Override
    protected void configure() {
        install(new ServiceConstantsModule());
        bind(RemoteStorageService.class).to(AwsRemoteStorageService.class);
        bind(RecordingService.class).to(RecordingServiceImpl.class);
        bind(ClipManager.class).to(ClipManagerImpl.class);
        bind(UrlShortener.class).to(BitlyUrlShortener.class);
        bind(WelcomeManager.class).to(PersistenceWelcomeManagerImpl.class);
        bind(Messager.class).to(MessagerImpl.class);
    }

    @Provides
    @Singleton
    AwsCredentialsProvider credentialsProvider() {
       return DefaultCredentialsProvider.create();
    }

    @Provides
    @Singleton
    SqsClient sqsClient (@Named("sqs.region") String region) {
        return SqsClient.builder()
                .region(Region.of(region))
                .build();
    }

    @Provides
    @Named("s3.client.download")
    @Singleton
    S3Client downloadS3Client(AwsCredentialsProvider provider,
                              @Named("s3.client.region.download") String region) {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(provider)
                .build();
    }

    @Provides
    @Named("s3.client.upload")
    @Singleton
    S3Client uploadS3Client(AwsCredentialsProvider provider,
                            @Named("s3.client.region.upload") String region) {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(provider)
                .build();
    }

    @Provides
    @Singleton
    S3Presigner presigner(AwsCredentialsProvider provider,
                          @Named("s3.client.region.upload") String region) {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(provider)
                .build();
    }

    @Provides
    @Singleton
    HttpRequestFactory requestFactory() {
        return new NetHttpTransport().createRequestFactory();
    }

    @Provides
    @Singleton
    DynamoDbClient dynamoDbClient(@Named("dynamodb.region") String region) {
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .build();
    }

    @Provides @Singleton DataPersistenceAdaptor<AuthRecord> dynamoAuthRecordPersistenceAdaptor(
            DynamoDbClient client,
            MappingFactory<AuthRecord> factory) {
        return new DynamoPersistenceAdaptorImpl<>(client, factory);
    }

    @Provides
    @Singleton
    MappingFactory<AuthRecord> authRecordMappingFactory(@Named("mapping.factory.table.auth") String table) {
        return new AuthRecordMappingFactory(table);
    }

    @Provides @Singleton DataPersistenceAdaptor<WelcomeRecord> welcomeRecordDataPersistenceAdaptor(
            DynamoDbClient client,
            MappingFactory<WelcomeRecord> factory) {
        return new DynamoPersistenceAdaptorImpl<>(client, factory);
    }

    @Provides
    @Singleton
    MappingFactory<WelcomeRecord> welcomeRecordMappingFactory(@Named("mapping.factory.table.welcome") String table) {
        return new WelcomeRecordMappingFactory(table);
    }

    @Provides @Singleton DataPersistenceAdaptor<Recording> recordingDataPersistenceAdaptor(
            DynamoDbClient client,
            MappingFactory<Recording> factory) {
        return new DynamoPersistenceAdaptorImpl<>(client, factory);
    }

    @Provides @Singleton
    MappingFactory<Recording> recordingMappingFactory(@Named("mapping.factory.table.recording") String table) {
        return new RecordingMappingFactory(table);
    }

    @Provides
    @Singleton
    SqsPollingWorker pollingWorker(RemoteStorageService remoteStorageService,
                                   SqsClient client,
                                   ClipManager clipManager,
                                   Messager messager,
                                   @Named("sqs.url") String url,
                                   @Named("sqs.poll.duration") int duration,
                                   @Named("clips.polling.enabled") boolean enabled) {
        logger.info("Polling enabled: " + enabled);
        if (enabled)
            return new SqsPollingWorker(remoteStorageService, client, clipManager, messager, url, duration);
        else
            return null;
    }
}
