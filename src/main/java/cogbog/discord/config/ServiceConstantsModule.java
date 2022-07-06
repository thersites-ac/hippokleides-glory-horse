package cogbog.discord.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;
import java.time.Duration;

public class ServiceConstantsModule extends AbstractModule {

    @Provides
    @Named("s3.uploads.ttl")
    Duration timeToLive() {
        return Duration.ofMinutes(10);
    }

    @Provides
    @Named("shortener.auth.token")
    String shortenerAuthToken() {
        return System.getProperty("shortener.auth.token");
    }
}