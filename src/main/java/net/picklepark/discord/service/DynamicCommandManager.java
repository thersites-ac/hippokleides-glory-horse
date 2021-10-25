package net.picklepark.discord.service;

import net.picklepark.discord.command.DiscordCommand;

public interface DynamicCommandManager {
    public DiscordCommand lookup(String command);
    public void put(String name, DiscordCommand command);
}
