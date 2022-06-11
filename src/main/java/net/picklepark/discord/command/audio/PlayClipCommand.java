package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.UnimplementedException;

public class PlayClipCommand implements net.picklepark.discord.command.DiscordCommand {

    private final String path;

    public PlayClipCommand(String path) {
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

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        throw new UnimplementedException();
    }

    @Override
    public String helpMessage() {
        throw new UnimplementedException();
    }

    @Override
    public String userInput() {
        return ".*";
    }
}
