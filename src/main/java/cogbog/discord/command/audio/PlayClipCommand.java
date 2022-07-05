package cogbog.discord.command.audio;

import cogbog.discord.command.DiscordCommand;
import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.NotEnoughQueueCapacityException;
import cogbog.discord.exception.UnimplementedException;

public class PlayClipCommand implements DiscordCommand {

    private static final String TOO_MANY_CLIPS_QUEUED_EXCEPTION = "Stop it, I'm just one bot!";
    private static final String INPUT_STRING = "<clip>";
    private final String path;

    public PlayClipCommand(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public void execute(MessageReceivedActions actions) {
        if (!actions.isConnected()) {
            actions.connect();
        }
        try {
            actions.queue(path);
        } catch (NotEnoughQueueCapacityException ex) {
            actions.send(TOO_MANY_CLIPS_QUEUED_EXCEPTION);
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
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
        return INPUT_STRING;
    }
}
