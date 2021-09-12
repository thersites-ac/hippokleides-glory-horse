package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput(".*")
public class ClipCommand implements DiscordCommand {

    private final String path;

    public ClipCommand(String path) {
        this.path = path;
    }

    @Override
    public void execute(DiscordActions actions) {
        actions.queue(path);
    }
}
