package tools;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.exception.AlreadyAdminException;
import net.picklepark.discord.exception.AuthException;
import net.picklepark.discord.exception.NoOwnerException;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.service.AuthManager;

import java.io.IOException;
import java.util.*;

public class InMemoryAuthManager implements AuthManager {

    private final Map<String, Set<Long>> admins;
    private final Map<String, Set<Long>> bans;

    public InMemoryAuthManager() {
        admins = new HashMap<>();
        bans = new HashMap<>();
    }

    @Override
    public boolean isActionAuthorized(MessageReceivedActions actions, AuthLevel level) {
        long author = actions.getAuthorId();
        String guild = actions.getGuildId();
        if (bans.getOrDefault(guild, Collections.emptySet()).contains(author))
            return false;
        AuthLevel authorLevel = getUserLevel(author, guild);
        try {
            if (actions.getGuildOwnerId() == author)
                authorLevel = AuthLevel.OWNER;
        } catch (NoOwnerException e) { }
        return authorLevel.compareTo(level) >= 0;
    }

    private AuthLevel getUserLevel(long user, String guild) {
        var guildAdmins = admins.get(guild);
        if (guildAdmins == null)
            return AuthLevel.USER;
        return guildAdmins.contains(user)? AuthLevel.ADMIN: AuthLevel.USER;
    }

    @Override
    public void addAdmin(String guildId, long user) throws IOException, AlreadyAdminException {
        admins.computeIfAbsent(guildId, g -> new HashSet<>());
        admins.get(guildId).add(user);
    }

    @Override
    public void demote(long user, MessageReceivedActions actions) throws IOException, AuthException {
        String guildId = actions.getGuildId();
        if (admins.containsKey(guildId)) {
            admins.get(guildId).remove(user);
        }
    }

    @Override
    public void ban(String guildId, long userId) {
        bans.computeIfAbsent(guildId, g -> new HashSet<>());
        bans.get(guildId).add(userId);
    }

    @Override
    public void unban(String guildId, long userId) {
        var guildBans = bans.get(guildId);
        if (guildBans != null) {
            guildBans.remove(userId);
        }
    }
}
