package net.picklepark.discord.service.impl;

import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.service.WelcomeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class WelcomeManagerImpl extends JavaConfigManager<Map<String, Map<Long, LocalClip>>> implements WelcomeManager {

    private static final String WELCOMES_KEY = "discord-bot-welcomes-config";
    private static final Logger logger = LoggerFactory.getLogger(WelcomeManagerImpl.class);

    private final Map<String, Map<Long, LocalClip>> welcomes;

    @Inject
    public WelcomeManagerImpl(@Named("s3.bucket.config") String configBucket,
                              @Named("s3.client.config") S3Client configFetcher) {
        super(configBucket, configFetcher, WELCOMES_KEY);
        Map<String, Map<Long, LocalClip>> tmp;
        try {
            tmp = getRemote();
        } catch (IOException e) {
            logger.error("While fetching welcomes", e);
            tmp = new ConcurrentHashMap<>();
        }
        welcomes = tmp;
    }

    @Override
    public LocalClip welcome(long user, String guild) {
        return Optional.ofNullable(welcomes.get(guild))
                .map(m -> m.get(user))
                .orElse(null);
    }

    @Override
    public void set(long user, String guild, LocalClip clip) throws IOException {
        logger.info(String.format("Welcoming %s in %s with %s", user, guild, clip));
        welcomes.computeIfAbsent(guild, g -> new ConcurrentHashMap<>());
        welcomes.get(guild).put(user, clip);
        persist(welcomes);
    }
}
