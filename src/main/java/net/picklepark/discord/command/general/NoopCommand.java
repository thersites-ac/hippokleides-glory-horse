package net.picklepark.discord.command.general;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;

public class NoopCommand implements DiscordCommand {

    public NoopCommand() {}

    @Override
    public void execute(DiscordActions actions) {
    }
}
