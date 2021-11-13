package net.picklepark.discord.service.impl;

import net.picklepark.discord.service.AuthConfigService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TestConfigService implements AuthConfigService {
    private final ConcurrentHashMap<String, Set<Long>> admins;

    public TestConfigService() {
        admins = new ConcurrentHashMap<>();
    }

    @Override
    public ConcurrentHashMap<String, Set<Long>> getCurrentAdmins() {
        return new ConcurrentHashMap<>(Collections.unmodifiableMap(admins));
    }

    @Override
    public void persistAdmins(String guildName, long user) {
        admins.computeIfAbsent(guildName, key -> new HashSet<>());
        admins.get(guildName).add(user);
    }
}
