package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.exception.NotEnoughQueueCapacityException;
import cogbog.discord.model.AuthLevel;

public class RamRanchCommand implements DiscordCommand {

    private static final String RAM_RANCH_URL = "https://www.youtube.com/watch?v=MADvxFXWvwE";
    private static final String SKIP_TRACKS_EXCEPTION = "Do ~skip a few times to clear out space for this important piece of art";

    @Override
    public void execute(MessageReceivedActions actions) {
        if (!actions.isConnected()) {
            actions.connect();
        }
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
