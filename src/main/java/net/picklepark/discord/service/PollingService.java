package net.picklepark.discord.service;

import net.picklepark.discord.command.DiscordCommand;

public interface PollingService {
    public void expect(String key);
    public DiscordCommand lookup(String command);
}
