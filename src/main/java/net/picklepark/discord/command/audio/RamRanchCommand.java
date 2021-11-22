package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;

public class RamRanchCommand implements DiscordCommand {

    private static final String RAM_RANCH_URL = "https://www.youtube.com/watch?v=MADvxFXWvwE";

    @Override
    public void execute(DiscordActions actions) {
        actions.connect();
        actions.queue(RAM_RANCH_URL);
        actions.send("enjoy :)");
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "ramranch";
    }

    @Override
    public String helpMessage() {
        return "RAM RANCH REALLY ROCKS";
    }

    @Override
    public String userInput() {
        return "ramranch";
    }
}
