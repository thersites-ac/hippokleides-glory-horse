package net.picklepark.discord.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;
import java.time.Duration;

public class ServiceConstantsModule extends AbstractModule {

    @Provides
    @Named("sqs.poll.interval")
    long sqsPollInterval() {
        return 5000;
    }

    @Provides
    @Named("sqs.url")
    String sqsUrl() {
        return "https://sqs.us-east-2.amazonaws.com/166605477498/TrimmedRecordingQueue";
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

    @Provides
    @Named("clips.directory")
    String clipsDirectory() {
        return "clips";
    }

}