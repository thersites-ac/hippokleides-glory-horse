package net.picklepark.discord.service.impl;

import net.picklepark.discord.service.AuthConfigService;

import java.util.Set;

public class SpyAuthService extends AuthServiceImpl {
    public SpyAuthService(AuthConfigService configService) {
        super(configService);
    }

    public Set<Long> getAdminsFor(String guild) {
        return admins.get(guild);
    }
}
