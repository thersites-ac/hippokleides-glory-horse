package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;

import java.io.IOException;

public interface DiscordCommand {
    void execute(DiscordActions actions) throws IOException;
}
