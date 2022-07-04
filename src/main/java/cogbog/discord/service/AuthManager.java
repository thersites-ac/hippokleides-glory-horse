package cogbog.discord.service;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.exception.AuthLevelConflictException;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.AlreadyAdminException;
import cogbog.discord.exception.AuthException;

import java.io.IOException;

public interface AuthManager {
    // fixme: decouple this from `MessageReceivedActions`
    boolean isActionAuthorized(MessageReceivedActions actions, AuthLevel level);
    void addAdmin(String guildId, long user) throws IOException, AlreadyAdminException;
    void demote(long user, MessageReceivedActions actions) throws IOException, AuthException;
    void ban(String guildId, long userId) throws IOException;
    void unban(String guildId, long userId) throws IOException, AuthLevelConflictException;
}