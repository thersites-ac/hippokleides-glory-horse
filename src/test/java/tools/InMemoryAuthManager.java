package tools;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.exception.AlreadyAdminException;
import net.picklepark.discord.exception.AuthException;
import net.picklepark.discord.exception.NoOwnerException;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.service.AuthManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryAuthManager implements AuthManager {

    private final Map<String, Map<Long, AuthLevel>> admins;

    public InMemoryAuthManager() {
        admins = new HashMap<>();
    }

    @Override
    public boolean isActionAuthorized(MessageReceivedActions actions, AuthLevel level) {
        long author = actions.getAuthorId();
        String guild = actions.getGuildId();
        AuthLevel authorLevel = getUserLevel(author, guild);
        try {
            if (actions.getGuildOwnerId() == author)
                authorLevel = AuthLevel.OWNER;
        } catch (NoOwnerException e) { }
        return authorLevel.compareTo(level) >= 0;
    }

    private AuthLevel getUserLevel(long user, String guild) {
        var guildLevels = admins.get(guild);
        if (guildLevels == null)
            return AuthLevel.USER;
        var level = guildLevels.get(user);
        return level == null? AuthLevel.USER: level;
    }

    @Override
    public void addAdmin(String guildId, long user) throws IOException, AlreadyAdminException {
        admins.computeIfAbsent(guildId, g -> new HashMap<>());
        admins.get(guildId).put(user, AuthLevel.ADMIN);
    }

    @Override
    public void demote(long user, MessageReceivedActions actions) throws IOException, AuthException {
        String guildId = actions.getGuildId();
        if (admins.containsKey(guildId)) {
            admins.get(guildId).remove(user);
        }
    }
}
