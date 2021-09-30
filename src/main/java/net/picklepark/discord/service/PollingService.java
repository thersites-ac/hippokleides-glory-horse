package net.picklepark.discord.service;

import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

// FIXME: get rid of the lookup method and make expect async so I can attach a callback to it
public interface PollingService {
    public void expect(String key);
    public DiscordCommand lookup(String command);
}
