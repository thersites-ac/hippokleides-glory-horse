package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.service.AuthService;

public class RubberstampAuthService implements AuthService {
    @Override
    public boolean isActionAuthorized(DiscordActions actions, Auth.Level level) {
        return true;
    }

    @Override
    public void addAdmin(String channelName, long user) {
    }
}
