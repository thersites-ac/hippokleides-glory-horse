package net.picklepark.discord.service.impl;

import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.service.DynamicCommandManager;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DynamicCommandManagerImpl implements DynamicCommandManager {

    private final ConcurrentHashMap<String, DiscordCommand> store;

    public DynamicCommandManagerImpl() {
        store = new ConcurrentHashMap<>();
    }

    @Override
    public DiscordCommand lookup(String command) {
        return store.get(command);
    }

    @Override
    public void put(String name, DiscordCommand command) {
        store.put(name, command);
    }

    @Override
    public Collection<String> getAllCommandNames() {
        return store.keySet();
    }
}
