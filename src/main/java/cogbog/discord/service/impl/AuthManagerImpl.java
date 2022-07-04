package cogbog.discord.service.impl;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.exception.*;
import cogbog.discord.model.AuthLevel;
import io.netty.util.internal.ConcurrentSet;
import cogbog.discord.service.AuthManager;
import cogbog.discord.service.AuthConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static cogbog.discord.constants.Names.AUTH_BAN_PERSISTER;

@Singleton
public class AuthManagerImpl implements AuthManager {
    private static final Logger logger = LoggerFactory.getLogger(AuthManagerImpl.class);
    protected final Map<String, Set<Long>> admins;
    private final AuthConfigService configService;
    private final Map<String, Set<Long>> bans;
    private final JavaConfigManager<Map<String, Set<Long>>> banPersister;

    // fixme: kind of gross how the two parameters are basically identical
    @Inject
    public AuthManagerImpl(AuthConfigService configService,
                           @Named(AUTH_BAN_PERSISTER) JavaConfigManager<Map<String, Set<Long>>> banPersister) {
        this.banPersister = banPersister;
        this.configService = configService;

        Map<String, Set<Long>> tempAdmins = new ConcurrentHashMap<>();
        try {
            tempAdmins = new ConcurrentHashMap<>(configService.getCurrentAdmins());
        } catch (Exception e) {
            logger.error("While initializing admins", e);
        }
        admins = tempAdmins;

        Map<String, Set<Long>> tempBans = new ConcurrentHashMap<>();
        try {
            tempBans = new ConcurrentHashMap<>(banPersister.getRemote());
        } catch (Exception e) {
            logger.error("While initializing bans", e);
        }
        bans = tempBans;

    }

    @Override
    public boolean isActionAuthorized(MessageReceivedActions actions, AuthLevel level) {
        if (isBanned(actions.getGuildId(), actions.getAuthorId()))
            return false;
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

    private boolean isBanned(String guildId, long authorId) {
        return bans.getOrDefault(guildId, Collections.emptySet()).contains(authorId);
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

    @Override
    public void ban(String guildId, long userId) throws IOException {
        bans.computeIfAbsent(guildId, g -> new ConcurrentSet<>()).add(userId);
        banPersister.persist(bans);
    }

    @Override
    public void unban(String guildId, long userId) throws IOException {
        Set<Long> guildBans = bans.get(guildId);
        if (guildBans != null && guildBans.contains(userId)) {
            guildBans.remove(userId);
            banPersister.persist(bans);
        }
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