package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.exception.AlreadyAdminException;
import net.picklepark.discord.exception.AuthException;
import net.picklepark.discord.exception.NoOwnerException;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.service.AuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceAuthManagerImpl implements AuthManager {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceAuthManagerImpl.class);

    private final Map<String, Map<Long, AuthLevel>> cache;

    public PersistenceAuthManagerImpl() {
        cache = new ConcurrentHashMap<>();
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
        var guildLevels = cache.get(guild);
        if (guildLevels != null && guildLevels.containsKey(actor)) {
            return guildLevels.get(actor).satisfies(level);
        } else {
            return AuthLevel.USER.satisfies(level);
        }
    }

    @Override
    public void addAdmin(String guildId, long user) throws IOException, AlreadyAdminException {
        cache.computeIfAbsent(guildId, g -> new ConcurrentHashMap<>()).put(user, AuthLevel.ADMIN);
    }

    @Override
    public void demote(long user, MessageReceivedActions actions) throws IOException, AuthException {
        Optional.ofNullable(cache.get(actions.getGuildId()))
                .map(guildLevels -> guildLevels.remove(user));
    }

    @Override
    public void ban(String guildId, long userId) throws IOException {
    }

    @Override
    public void unban(String guildId, long userId) throws IOException {
    }
}
