package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.model.AuthLevel;

public class NukeQueueCommand implements DiscordCommand {

    public static final String CONFIRMATION_MESSAGE = "Gone. All gone.";

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        actions.nuke();
        actions.send(CONFIRMATION_MESSAGE);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "nuke";
    }

    @Override
    public String helpMessage() {
        return "Remove all queued audio";
    }

    @Override
    public String userInput() {
        return "nuke";
    }
}
