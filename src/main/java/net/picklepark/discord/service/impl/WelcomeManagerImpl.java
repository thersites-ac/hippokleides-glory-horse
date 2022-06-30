package net.picklepark.discord.service.impl;

import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.service.WelcomeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class WelcomeManagerImpl extends JavaConfigManager<Map<String, Map<String, LocalClip>>> implements WelcomeManager {

    private static final String WELCOMES_KEY = "discord-bot-welcomes-config";
    private static final Logger logger = LoggerFactory.getLogger(WelcomeManagerImpl.class);

    private final Map<String, Map<String, LocalClip>> welcomes;

    @Inject
    public WelcomeManagerImpl(@Named("s3.bucket.config") String configBucket,
                              @Named("s3.client.config") S3Client configFetcher) {
        super(configBucket, configFetcher, WELCOMES_KEY);
        Map<String, Map<String, LocalClip>> tmp;
        try {
            tmp = getRemote();
        } catch (IOException e) {
            logger.error("While fetching welcomes", e);
            tmp = new ConcurrentHashMap<>();
        }
        welcomes = tmp;
    }

    @Override
    public LocalClip welcome(String user, String guild) {
        return Optional.ofNullable(welcomes.get(user))
                .map(m -> m.get(guild))
                .orElse(null);
    }

    @Override
    public void set(String user, String guild, LocalClip clip) throws IOException {
        logger.info(String.format("Welcoming %s in %s with %s", user, guild, clip));
        welcomes.computeIfAbsent(user, u -> new ConcurrentHashMap<>());
        welcomes.get(user).put(guild, clip);
        persist(welcomes);
    }
}
