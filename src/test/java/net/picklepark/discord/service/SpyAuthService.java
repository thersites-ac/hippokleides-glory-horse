package net.picklepark.discord.service;

import net.picklepark.discord.service.impl.AuthServiceImpl;

import java.util.Set;

public class SpyAuthService extends AuthServiceImpl {
    public Set<Long> getAdminsFor(String guild) {
        return admins.get(guild);
    }
}
