package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.UserInput;

@UserInput(".*")
public class ClipCommand implements net.picklepark.discord.command.DiscordCommand {

    private final String path;

    public ClipCommand(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public void execute(DiscordActions actions) {
        actions.connect();
        actions.queue(path);
    }
}
