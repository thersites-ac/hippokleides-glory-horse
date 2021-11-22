package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.service.AuthService;

public class RubberstampAuthService implements AuthService {
    @Override
    public boolean isActionAuthorized(DiscordActions actions, AuthLevel level) {
        return true;
    }

    @Override
    public void addAdmin(String channelName, long user) {
    }

    @Override
    public void demote(long user, DiscordActions actions) {
    }
}
