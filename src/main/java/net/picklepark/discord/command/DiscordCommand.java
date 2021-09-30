package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.exception.DiscordCommandException;

public interface DiscordCommand {
    void execute(DiscordActions actions) throws DiscordCommandException;
}
