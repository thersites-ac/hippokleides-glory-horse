package cogbog.discord.config;

import cogbog.discord.constants.Names;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;
import java.time.Duration;

public class ServiceConstantsModule extends AbstractModule {

    @Provides
    @Named("sqs.poll.interval")
    long sqsPollInterval() {
        return 500;
    }

    @Provides
    @Named("sqs.url")
    String sqsUrl() {
        return "https://sqs.us-east-2.amazonaws.com/166605477498/TrimmedRecordingQueue";
    }

    @Provides
    @Named("s3.bucket.uploads")
    String s3UploadsBucket() {
        return "discord-recordings";
    }

    @Provides
    @Named("s3.bucket.trimmed")
    String s3TrimmedBucket() {
        return "discord-output";
    }

    @Provides
    @Named(Names.CLIP_DURATION)
    int recordingClipDuration() {
        return 30000;
    }

    @Provides
    @Named("s3.uploads.ttl")
    Duration timeToLive() {
        return Duration.ofMinutes(10);
    }

    @Provides
    @Named("clips.directory")
    String clipsDirectory() {
        return "clips";
    }

    @Provides
    @Named("shortener.group.guid")
    String groupGuid() {
        return "Blb7g34TKlu";
    }

    @Provides
    @Named("shortener.auth.token")
    String shortenerAuthToken() {
        return System.getProperty("shortener.auth.token");
    }

    @Provides
    @Named("shortener.endpoint")
    String shortenerEndpoint() {
        return "https://api-ssl.bitly.com/v4/shorten";
    }

    @Provides
    @Named(Names.S3_BUCKET_CONFIG)
    String configBucket() {
        return "discord-config";
    }

}