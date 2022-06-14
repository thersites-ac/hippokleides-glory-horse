package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.AuthLevelConflictException;
import net.picklepark.discord.exception.NoOwnerException;
import net.picklepark.discord.service.AuthManager;
import net.picklepark.discord.service.AuthConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AuthManagerImpl implements AuthManager {
    private static final Logger logger = LoggerFactory.getLogger(AuthManagerImpl.class);
    protected final ConcurrentHashMap<String, Set<Long>> admins;
    private final AuthConfigService configService;

    @Inject
    public AuthManagerImpl(AuthConfigService configService) {
        ConcurrentHashMap<String, Set<Long>> tempAdmins;
        this.configService = configService;
        try {
            tempAdmins = new ConcurrentHashMap<>(configService.getCurrentAdmins());
        } catch (Exception e) {
            logger.error("While initializing admins", e);
            tempAdmins = new ConcurrentHashMap<>();
        }
        admins = tempAdmins;
    }

    @Override
    public boolean isActionAuthorized(MessageReceivedActions actions, AuthLevel level) {
        switch (level) {
            case ANY:
                return true;
            case OWNER:
                return authorIsOwner(actions);
            case ADMIN:
                return hasAdminPrivileges(actions);
            default:
                return false;
        }
    }

    private boolean authorIsOwner(MessageReceivedActions actions) {
        return isOwner(actions, actions.getAuthor().getIdLong());
    }

    @Override
    public void addAdmin(String guildName, long user) throws IOException {
        admins.computeIfAbsent(guildName, key -> new HashSet<>());
        if (admins.get(guildName).add(user)) {
            configService.persistAdmins(admins);
            logger.info("Updated persistent admins after user {} was added in guild {}", user, guildName);
        }
    }

    @Override
    public void demote(long user, MessageReceivedActions actions) throws AuthLevelConflictException, IOException {
        if (lookupOwner(actions) == user)
            throw new AuthLevelConflictException(user);
        if (isAdmin(actions, user)) {
            admins.get(actions.getGuildId()).remove(user);
            configService.persistAdmins(admins);
        } else
            throw new AuthLevelConflictException(user);
    }

    private boolean hasAdminPrivileges(MessageReceivedActions actions) {
        long authorId = actions.getAuthor().getIdLong();
        return isAdmin(actions, authorId) || isOwner(actions, authorId);
    }

    private boolean isAdmin(MessageReceivedActions actions, long id) {
        Set<Long> guildAdmins = admins.get(actions.getGuildId());
        return guildAdmins != null && guildAdmins.contains(id);
    }

    private boolean isOwner(MessageReceivedActions actions, Long id) {
        return lookupOwner(actions) == id;
    }

    private long lookupOwner(MessageReceivedActions actions) {
        try {
            return actions.getOwner().getIdLong();
        } catch (NoOwnerException e) {
            return -1;
        }
    }

}