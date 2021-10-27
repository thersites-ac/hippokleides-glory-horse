package net.picklepark.discord.service;

import net.picklepark.discord.command.DiscordCommand;

import java.util.Collection;

public interface DynamicCommandManager {
    DiscordCommand lookup(String command);
    void put(String name, DiscordCommand command);
    Collection<String> getAllCommandNames();
    void delete(String clip);
}
