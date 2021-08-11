package net.picklepark.discord.command;

import java.io.IOException;

public interface DiscordCommand {
    public void execute() throws IOException;
}
