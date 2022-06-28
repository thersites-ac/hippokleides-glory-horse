package net.picklepark.discord.service;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.exception.NoOwnerException;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.exception.AlreadyAdminException;
import net.picklepark.discord.exception.AuthException;

import java.io.IOException;

public interface AuthManager {
    // fixme: decouple this from `MessageReceivedActions`
    boolean isActionAuthorized(MessageReceivedActions actions, AuthLevel level);
    void addAdmin(String guildId, long user) throws IOException, AlreadyAdminException;
    void demote(long user, MessageReceivedActions actions) throws IOException, AuthException;
    void ban(String guildId, long userId) throws IOException;
    void unban(String guildId, long userId) throws IOException;
}