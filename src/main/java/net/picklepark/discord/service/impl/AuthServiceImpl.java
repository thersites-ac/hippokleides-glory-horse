package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.NoOwnerException;
import net.picklepark.discord.service.AuthService;
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
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    protected final ConcurrentHashMap<String, Set<Long>> admins;
    private final AuthConfigService configService;

    @Inject
    public AuthServiceImpl(AuthConfigService configService) {
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
    public boolean isActionAuthorized(DiscordActions actions, AuthLevel level) {
        switch (level) {
            case ANY:
                return true;
            case OWNER:
                return owner(actions);
            case ADMIN:
                return admin(actions);
            default:
                return false;
        }
    }

    @Override
    public void addAdmin(String guildName, long user) {
        admins.computeIfAbsent(guildName, key -> new HashSet<>());
        if (admins.get(guildName).add(user)) try {
            configService.persistAdmins(admins);
            logger.info("Updated persistent admins after user {} was added in guild {}", user, guildName);
        } catch (IOException e) {
            logger.error("While persisting admins for guild " + guildName, e);
        }
    }

    private boolean admin(DiscordActions actions) {
        Set<Long> guildAdmins = admins.get(actions.getGuildName());
        boolean userIsAdmin = guildAdmins != null && guildAdmins.contains(actions.getAuthor().getIdLong());
        return userIsAdmin || owner(actions);
    }

    private boolean owner(DiscordActions actions) {
        try {
            return actions.getOwner().getIdLong() == actions.getAuthor().getIdLong();
        } catch (NoOwnerException e) {
            logger.warn("Unowned channel", e);
            return false;
        }
    }

}