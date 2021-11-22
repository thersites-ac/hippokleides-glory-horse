package net.picklepark.discord.service;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;

public interface AuthService {
    boolean isActionAuthorized(DiscordActions actions, AuthLevel level);
    void addAdmin(String channelName, long user);
}