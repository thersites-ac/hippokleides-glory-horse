package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.NotEnoughQueueCapacityException;

public class RamRanchCommand implements DiscordCommand {

    private static final String RAM_RANCH_URL = "https://www.youtube.com/watch?v=MADvxFXWvwE";
    private static final String SKIP_TRACKS_EXCEPTION = "Do ~skip a few times to clear out space for this important piece of art";

    @Override
    public void execute(DiscordActions actions) {
        actions.connect();
        try {
            actions.queue(RAM_RANCH_URL);
            actions.send("enjoy :)");
        } catch (NotEnoughQueueCapacityException ex) {
            actions.send(SKIP_TRACKS_EXCEPTION);
        }
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
