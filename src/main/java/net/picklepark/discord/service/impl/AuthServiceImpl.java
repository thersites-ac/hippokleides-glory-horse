package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.exception.NoOwnerException;
import net.picklepark.discord.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    protected final ConcurrentHashMap<String, Set<Long>> admins;

    public AuthServiceImpl() {
        admins = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isActionAuthorized(DiscordActions actions, Auth.Level level) {
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
        admins.get(guildName).add(user);
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