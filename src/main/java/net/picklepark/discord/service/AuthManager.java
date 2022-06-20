package net.picklepark.discord.service;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.AlreadyAdminException;
import net.picklepark.discord.exception.AuthException;
import net.picklepark.discord.exception.AuthLevelConflictException;

import java.io.IOException;

public interface AuthManager {
    boolean isActionAuthorized(MessageReceivedActions actions, AuthLevel level);
    void addAdmin(String channelName, long user) throws IOException, AlreadyAdminException;
    void demote(long user, MessageReceivedActions actions) throws IOException, AuthException;
}