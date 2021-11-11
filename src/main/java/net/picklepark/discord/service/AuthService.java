package net.picklepark.discord.service;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;

public interface AuthService {
    boolean isActionAuthorized(DiscordActions actions, Auth.Level level);
    void addAdmin(String channelName, long user);
}
