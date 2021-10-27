package net.picklepark.discord.service.impl;

import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.service.DynamicCommandManager;
import net.picklepark.discord.service.RemoteStorageService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DynamicCommandManagerImpl implements DynamicCommandManager {

    private final ConcurrentHashMap<String, DiscordCommand> localStore;

    public DynamicCommandManagerImpl() {
        localStore = new ConcurrentHashMap<>();
    }

    @Override
    public DiscordCommand lookup(String command) {
        return localStore.get(command);
    }

    @Override
    public void put(String name, DiscordCommand command) {
        localStore.put(name, command);
    }

    @Override
    public Collection<String> getAllCommandNames() {
        return localStore.keySet();
    }

    @Override
    public void delete(String clip) {
        localStore.remove(clip);
    }
}
