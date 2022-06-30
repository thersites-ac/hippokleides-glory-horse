package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.DataPersistenceAdaptor;
import net.picklepark.discord.exception.DataMappingException;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.model.WelcomeRecord;
import net.picklepark.discord.service.WelcomeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceWelcomeManagerImpl implements WelcomeManager {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceWelcomeManagerImpl.class);
    // a gross hack, but ConcurrentHashMap won't store nulls
    private static final LocalClip NO_CLIP = LocalClip.builder().build();

    private final Map<String, Map<String, LocalClip>> cache;
    private final DataPersistenceAdaptor<WelcomeRecord> data;

    public PersistenceWelcomeManagerImpl(DataPersistenceAdaptor<WelcomeRecord> data) {
        cache = new ConcurrentHashMap<>();
        this.data = data;
    }

    @Override
    public LocalClip welcome(String user, String guild) {
        if (cache.containsKey(guild) && cache.get(guild).containsKey(user)) {
            return cache.get(guild).get(user);
        } else {
            var remoteKey = key(user, guild);
            try {
                var remoteWelcome = data.read(remoteKey);
                var clip = remoteWelcome == null? NO_CLIP: remoteWelcome.getLocalClip();
                cache.computeIfAbsent(guild, g -> new ConcurrentHashMap<>())
                        .put(user, clip);
                return clip == NO_CLIP? null: clip;
            } catch (DataMappingException e) {
                logger.error("While reading welcome from remote", e);
                return null;
            }
        }
    }

    @Override
    public void set(String user, String guild, LocalClip clip) throws IOException {
        cache.computeIfAbsent(guild, g -> new ConcurrentHashMap<>())
                .put(user, clip);
        var record = WelcomeRecord.builder()
                .guildId(guild)
                .userId(user)
                .localClip(clip)
                .build();
        data.write(record);
        logger.info("Wrote through welcome record: " + record.toString());
    }

    private Map<String, String> key(String userId, String guildId) {
        return Map.of(
                WelcomeRecord.USER_ID, userId,
                WelcomeRecord.GUILD_ID, guildId
        );
    }
}
