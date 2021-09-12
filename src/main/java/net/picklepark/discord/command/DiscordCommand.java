package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;

public interface DiscordCommand {
    void execute(DiscordActions actions) throws Exception;
}
