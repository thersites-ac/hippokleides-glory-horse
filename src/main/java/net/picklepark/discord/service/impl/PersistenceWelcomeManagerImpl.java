package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.DataPersistenceAdaptor;
import net.picklepark.discord.exception.DataMappingException;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.model.WelcomeRecord;
import net.picklepark.discord.persistence.WelcomeRecordMappingFactory;
import net.picklepark.discord.service.WelcomeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PersistenceWelcomeManagerImpl implements WelcomeManager {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceWelcomeManagerImpl.class);
    // a gross hack, but ConcurrentHashMap won't store nulls
    private static final LocalClip NO_CLIP = LocalClip.builder().build();

    private final Map<String, Map<Long, LocalClip>> cache;
    private final DataPersistenceAdaptor<WelcomeRecord> data;

    @Inject
    public PersistenceWelcomeManagerImpl(DataPersistenceAdaptor<WelcomeRecord> data) {
        cache = new ConcurrentHashMap<>();
        this.data = data;
    }

    @Override
    public LocalClip welcome(long user, String guild) {
        if (cache.containsKey(guild) && cache.get(guild).containsKey(user)) {
            var clip = cache.get(guild).get(user);
            return clip == NO_CLIP? null: clip;
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
    public void set(long user, String guild, LocalClip clip) {
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

    private Map<String, String> key(long userId, String guildId) {
        return Map.of(
                WelcomeRecordMappingFactory.USER_ID, userId + "",
                WelcomeRecordMappingFactory.GUILD_ID, guildId
        );
    }
}
