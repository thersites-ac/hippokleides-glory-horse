package net.picklepark.discord.service;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.AuthLevelConflictException;

import java.io.IOException;

public interface AuthService {
    boolean isActionAuthorized(DiscordActions actions, AuthLevel level);
    void addAdmin(String channelName, long user) throws IOException;
    void demote(long user, DiscordActions actions) throws AuthLevelConflictException, IOException;
}