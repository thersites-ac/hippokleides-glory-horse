package cogbog.discord.service.impl;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.exception.*;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.persistence.AuthRecordMappingFactory;
import cogbog.discord.service.AuthManager;
import cogbog.discord.adaptor.DataPersistenceAdaptor;
import cogbog.discord.model.AuthRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PersistenceAuthManagerImpl implements AuthManager {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceAuthManagerImpl.class);

    private final Map<String, Map<Long, AuthLevel>> cache;
    private final DataPersistenceAdaptor<AuthRecord> data;

    @Inject
    public PersistenceAuthManagerImpl(DataPersistenceAdaptor<AuthRecord> data) {
        cache = new ConcurrentHashMap<>();
        this.data = data;
    }

    @Override
    public boolean isActionAuthorized(MessageReceivedActions actions, AuthLevel level) {
        try {
            if (actions.getAuthorId() == actions.getGuildOwnerId())
                return true;
        } catch (NoOwnerException ex) {
            logger.warn("Ownerless guild", ex);
        }

        String guild = actions.getGuildId();
        long actor = actions.getAuthorId();
        return get(guild, actor).satisfies(level);
    }

    @Override
    public void addAdmin(String guildId, long user) throws IOException, AlreadyAdminException {
        set(guildId, user, AuthLevel.ADMIN);
    }

    @Override
    public void demote(long user, MessageReceivedActions actions) throws IOException, AuthException {
        changeLevelFromTo(AuthLevel.ADMIN, AuthLevel.USER, actions.getGuildId(), user);
    }

    @Override
    public void ban(String guildId, long userId) throws IOException {
        set(guildId, userId, AuthLevel.BANNED);
    }

    private void changeLevelFromTo(AuthLevel from, AuthLevel to, String guild, long user)
            throws AuthLevelConflictException {
        AuthLevel level = get(guild, user);
        if (level == from) {
            set(guild, user, to);
        } else {
            throw new AuthLevelConflictException(user);
        }
    }

    @Override
    public void unban(String guildId, long userId) throws IOException, AuthLevelConflictException {
        changeLevelFromTo(AuthLevel.BANNED, AuthLevel.USER, guildId, userId);
    }

    private void set(String guildId, long user, AuthLevel level) {
        var record = AuthRecord.builder()
                .guildId(guildId)
                .userId(user)
                .level(level)
                .build();
        data.write(record);
        cache.computeIfAbsent(guildId, g -> new ConcurrentHashMap<>()).put(user, level);
        logger.info("Wrote through record: " + record);
    }

    private AuthLevel get(String guildId, long actor) {
        var guildLevels = cache.get(guildId);
        if (guildLevels != null && guildLevels.containsKey(actor)) {
            return guildLevels.get(actor);
        } else {
            AuthRecord remote = null;
            try {
                remote = readFromCache(guildId, actor);
            } catch (DataMappingException ex) {
                logger.error("While reading AuthLevel from remote", ex);
            }
            return remote == null ? AuthLevel.USER : remote.getLevel();
        }
    }

    private AuthRecord readFromCache(String guildId, long actor) throws DataMappingException {
        var result = data.read(key(guildId, actor));
        if (result != null) {
            logger.info("Read a new entry from remote: " + result.toString());
            set(result.getGuildId(), result.getUserId(), result.getLevel());
        }
        return result;
    }

    private Map<String, String> key(String guildId, long actor) {
        return Map.of(
                AuthRecordMappingFactory.GUILD_ID, guildId,
                AuthRecordMappingFactory.USER_ID, actor + ""
        );
    }
}