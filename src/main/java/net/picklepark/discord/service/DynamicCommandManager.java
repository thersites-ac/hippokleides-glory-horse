package net.picklepark.discord.service;

import net.picklepark.discord.command.DiscordCommand;

import java.util.Collection;

public interface DynamicCommandManager {
    public DiscordCommand lookup(String command);
    public void put(String name, DiscordCommand command);
    public Collection<String> getAllCommandNames();
}
