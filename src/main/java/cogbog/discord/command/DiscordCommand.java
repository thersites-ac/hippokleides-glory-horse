package cogbog.discord.command;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.DiscordCommandException;

public interface DiscordCommand {
    void execute(MessageReceivedActions actions) throws DiscordCommandException;
    AuthLevel requiredAuthLevel();
    // todo: I can generally replace this with the DSL pattern now
    String example();
    String helpMessage();
    String userInput();
}
