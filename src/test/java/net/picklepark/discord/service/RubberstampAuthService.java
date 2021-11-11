package net.picklepark.discord.service;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;

public class RubberstampAuthService implements AuthService {
    @Override
    public boolean isActionAuthorized(DiscordActions actions, Auth.Level level) {
        return true;
    }

    @Override
    public void addAdmin(String channelName, long user) {
    }
}
