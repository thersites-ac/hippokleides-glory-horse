package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.exception.*;
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
            case USER:
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
        return isOwner(actions, actions.getAuthorId());
    }

    @Override
    public void addAdmin(String guildId, long user) throws IOException, AlreadyAdminException {
        admins.computeIfAbsent(guildId, key -> new HashSet<>());
        var guildAdmins = admins.get(guildId);
        if (guildAdmins.add(user)) {
            configService.persistAdmins(admins);
            logger.info("Updated persistent admins after user {} was added in guild {}", user, guildId);
        } else {
            throw new AlreadyAdminException(user);
        }
    }

    @Override
    public void demote(long user, MessageReceivedActions actions) throws AuthException, IOException {
        if (lookupOwner(actions) == user)
            throw new CannotDemoteSelfException();
        if (isAdmin(actions, user)) {
            admins.get(actions.getGuildId()).remove(user);
            configService.persistAdmins(admins);
        } else
            throw new AuthLevelConflictException(user);
    }

    private boolean hasAdminPrivileges(MessageReceivedActions actions) {
        long authorId = actions.getAuthorId();
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
            return actions.getGuildOwnerId();
        } catch (NoOwnerException e) {
            return -1;
        }
    }

}