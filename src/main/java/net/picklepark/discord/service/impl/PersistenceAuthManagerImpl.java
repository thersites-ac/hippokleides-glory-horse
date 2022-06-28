package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.exception.AlreadyAdminException;
import net.picklepark.discord.exception.AuthException;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.service.AuthManager;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceAuthManagerImpl implements AuthManager {

    private final Map<String, Map<Long, AuthLevel>> cache;

    public PersistenceAuthManagerImpl() {
        cache = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isActionAuthorized(MessageReceivedActions actions, AuthLevel level) {
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
    }

    @Override
    public void demote(long user, MessageReceivedActions actions) throws IOException, AuthException {
    }

    @Override
    public void ban(String guildId, long userId) throws IOException {
    }

    @Override
    public void unban(String guildId, long userId) throws IOException {
    }
}
